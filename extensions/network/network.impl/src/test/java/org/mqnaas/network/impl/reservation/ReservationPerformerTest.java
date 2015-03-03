package org.mqnaas.network.impl.reservation;

/*
 * #%L
 * MQNaaS :: Network Implementation
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceMetaData;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ServiceExecutionSchedulerException;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.api.scheduling.IServiceExecutionScheduler;
import org.mqnaas.core.api.scheduling.ServiceExecution;
import org.mqnaas.core.api.scheduling.Trigger;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.core.impl.scheduling.ServiceExecutionScheduler;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.reservation.IReservationAdministration;
import org.mqnaas.network.api.reservation.IReservationAdministration.ReservationState;
import org.mqnaas.network.api.reservation.IReservationPerformer;
import org.mqnaas.network.api.reservation.ReservationResource;
import org.mqnaas.network.api.reservation.ResourceReservationException;
import org.powermock.api.mockito.PowerMockito;

/**
 * <p>
 * Class containing tests for the {@link ReservationPerformer} implementation.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ReservationPerformerTest {

	private IReservationPerformer	reservationPerformer;

	IServiceExecutionScheduler		serviceExecutionScheduler;
	IServiceProvider				serviceProvider;

	IReservationAdministration		reservationAdministrationCapability;

	@Before
	public void prepareTest() throws SecurityException, IllegalArgumentException, IllegalAccessException, ServiceNotFoundException,
			ApplicationActivationException, InstantiationException {

		reservationPerformer = new ReservationPerformer();

		// mock necessary capabilities
		serviceExecutionScheduler = PowerMockito.mock(IServiceExecutionScheduler.class);
		serviceProvider = PowerMockito.mock(IServiceProvider.class);

		// inject mocked capabilities
		ReflectionTestHelper.injectPrivateField(reservationPerformer, serviceProvider, "serviceProvider");
		ReflectionTestHelper.injectPrivateField(reservationPerformer, serviceExecutionScheduler, "serviceExecutionScheduler");

		// inject resource
		IResource resource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));
		ReflectionTestHelper.injectPrivateField(reservationPerformer, resource, "resource");

		Mockito.when(serviceProvider.getService(Mockito.any(IResource.class), Mockito.anyString(), Mockito.<Class> anyVararg())).thenReturn(
				new MockService());

		reservationPerformer.activate();
	}

	/**
	 * <p>
	 * Test checks the {@link IReservationPerformer#performReservation(ReservationResource)} method. It performs the reservation of three
	 * {@link IRootResource}s.
	 * </p>
	 * <p>
	 * Asserts checks that:
	 * <ul>
	 * <li>The reservation is performed if it's in {@link ReservationState#PLANNED} state.</li>
	 * <li>The method schedules a service execution to finish the reservation</li>
	 * <li>After the service execution, the {@link ReservationResource} is in the {@link ReservationState#RESERVED} state</li>
	 * </ul>
	 * </p>
	 */
	@Test
	public void performReservationTest() throws ResourceReservationException, SecurityException, IllegalArgumentException, IllegalAccessException,
			ServiceExecutionSchedulerException, InstantiationException, URISyntaxException, CapabilityNotFoundException {

		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 5000L);

		// manually create reservation
		ReservationResource reservation = new ReservationResource();

		// create custom ReservationAdministration - used by serviceProvider
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));
		reservationAdministrationCapability.setState(ReservationState.PLANNED);

		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservation), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);

		Assert.assertFalse((((ReservationPerformer) reservationPerformer).scheduledFinishReservationServicesExecutions).containsKey(reservation));

		// REAL METHOD - perform a valid reservation
		reservationPerformer.performReservation(reservation);

		Mockito.verify(serviceExecutionScheduler, Mockito.times(1)).schedule(Mockito.any(ServiceExecution.class));
		Assert.assertEquals(ReservationState.RESERVED, reservationAdministrationCapability.getState());
		Assert.assertTrue((((ReservationPerformer) reservationPerformer).scheduledFinishReservationServicesExecutions).containsKey(reservation));
	}

	/**
	 * <p>
	 * Test checks the {@link IReservationPerformer#performReservation(ReservationResource)} method. It tries to perform a reservation of an already
	 * performed reservation (i.e. it's in {@link ReservationState#RESERVED} state), which should fail with an {@link IllegalStateException}.
	 * </p>
	 */
	@Test(expected = IllegalStateException.class)
	public void performAlreadyPerformedReservation() throws ResourceReservationException, SecurityException, IllegalArgumentException,
			IllegalAccessException, CapabilityNotFoundException, InstantiationException, URISyntaxException {
		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 5000L);

		// manually create reservation
		ReservationResource reservation = new ReservationResource();

		// create custom ReservationAdministration - used by serviceProvider
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));
		reservationAdministrationCapability.setState(ReservationState.RESERVED);

		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservation), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);

		// reservation capability does not contain any planned reservation
		reservationPerformer.performReservation(reservation);

	}

	/**
	 * <p>
	 * Test checks the {@link IReservationPerformer#cancelReservation(ReservationResource)} method. It releases the reservation of the three
	 * {@link IRootResource}s.
	 * </p>
	 * <p>
	 * Asserts checks that:
	 * <ul>
	 * <li>The cancellation is performed only if reservation is in {@link ReservationState#RESERVED} state.</li>
	 * <li>The method cancels the scheduled service that finished the reservation</li>
	 * <li>After the service execution, the {@link ReservationResource} is in the {@link ReservationState#CANCELLED} state</li>
	 * </ul>
	 * </p>
	 * 
	 */
	@Test
	public void cancelReservationTest() throws ResourceReservationException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, CapabilityNotFoundException, URISyntaxException, ServiceExecutionSchedulerException {

		// generate reservation object
		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 5000L);

		// manually create reservation
		ReservationResource reservation = new ReservationResource();

		// create custom ReservationAdministration - used by serviceProvider
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));
		reservationAdministrationCapability.setState(ReservationState.RESERVED);

		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservation), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);

		// manually inject the reservation into the scheduledFinishReservationServicesExecutions map
		Map<ReservationResource, ServiceExecution> scheduledFinishReservationServicesExecutions = new ConcurrentHashMap<ReservationResource, ServiceExecution>();
		scheduledFinishReservationServicesExecutions.put(reservation, new ServiceExecution(new MockService(), new DummyTrigger()));
		ReflectionTestHelper.injectPrivateField(reservationPerformer, scheduledFinishReservationServicesExecutions,
				"scheduledFinishReservationServicesExecutions");

		// check we correctly injected it..
		Assert.assertTrue((((ReservationPerformer) reservationPerformer).scheduledFinishReservationServicesExecutions).containsKey(reservation));

		// test and verify method
		reservationPerformer.cancelReservation(reservation);

		Assert.assertEquals(ReservationState.CANCELLED, reservationAdministrationCapability.getState());
		Assert.assertFalse((((ReservationPerformer) reservationPerformer).scheduledFinishReservationServicesExecutions).containsKey(reservation));
		Mockito.verify(serviceExecutionScheduler, Mockito.times(1)).cancel(Mockito.any(ServiceExecution.class));

	}

	/**
	 * <p>
	 * Test checks the {@link IReservationPerformer#cancelReservation(ReservationResource)} method. It tries to release still-not-reserved
	 * reservation, which should fail with an {@link IllegalStateException}.
	 * </p>
	 */
	@Test(expected = IllegalStateException.class)
	public void cancelPlannedReservation() throws ResourceReservationException, InstantiationException, IllegalAccessException, URISyntaxException,
			CapabilityNotFoundException {

		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 5000L);

		// manually create reservation
		ReservationResource reservation = new ReservationResource();

		// create custom ReservationAdministration - used by serviceProvider
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));
		reservationAdministrationCapability.setState(ReservationState.PLANNED);

		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservation), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);

		reservationPerformer.cancelReservation(reservation);
	}

	/**
	 * <p>
	 * Test checks the {@link IReservationPerformer#finishReservation(ReservationResource))} method. It releases the reservation of the three
	 * {@link IRootResource}s.
	 * </p>
	 * <p>
	 * Asserts checks that:
	 * <ul>
	 * <li>It's only performed only if reservation is in {@link ReservationState#RESERVED} state.</li>
	 * <li>The method does not cancel the scheduled service that finished the reservation (it does not make sense, since this service is only executed
	 * by the {@link ServiceExecutionScheduler}</li>
	 * <li>After the service execution, the {@link ReservationResource} is in the {@link ReservationState#FINISHED} state</li>
	 * </ul>
	 * </p>
	 * 
	 */
	@Test
	public void finishReservationTest() throws ResourceReservationException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, CapabilityNotFoundException, URISyntaxException, ServiceExecutionSchedulerException {

		// generate reservation object
		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 5000L);

		// manually create reservation
		ReservationResource reservation = new ReservationResource();

		// create custom ReservationAdministration - used by serviceProvider
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));
		reservationAdministrationCapability.setState(ReservationState.RESERVED);

		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservation), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);

		// manually inject the reservation into the scheduledFinishReservationServicesExecutions map
		Map<ReservationResource, ServiceExecution> scheduledFinishReservationServicesExecutions = new ConcurrentHashMap<ReservationResource, ServiceExecution>();
		scheduledFinishReservationServicesExecutions.put(reservation, new ServiceExecution(new MockService(), new DummyTrigger()));
		ReflectionTestHelper.injectPrivateField(reservationPerformer, scheduledFinishReservationServicesExecutions,
				"scheduledFinishReservationServicesExecutions");

		// check we correctly injected it..
		Assert.assertTrue((((ReservationPerformer) reservationPerformer).scheduledFinishReservationServicesExecutions).containsKey(reservation));

		// test and verify method
		reservationPerformer.finishReservation(reservation);

		Assert.assertEquals(ReservationState.FINISHED, reservationAdministrationCapability.getState());
		Assert.assertFalse((((ReservationPerformer) reservationPerformer).scheduledFinishReservationServicesExecutions).containsKey(reservation));
		Mockito.verify(serviceExecutionScheduler, Mockito.times(0)).cancel(Mockito.any(ServiceExecution.class));

	}

	/**
	 * <p>
	 * Test checks the {@link IReservationPerformer#finishReservation(ReservationResource)} method. It tries to release still-not-reserved
	 * reservation, which should fail with an {@link IllegalStateException}.
	 * </p>
	 */
	@Test(expected = IllegalStateException.class)
	public void FinishPlannedReservation() throws ResourceReservationException, InstantiationException, IllegalAccessException, URISyntaxException,
			CapabilityNotFoundException {

		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 5000L);

		// manually create reservation
		ReservationResource reservation = new ReservationResource();

		// create custom ReservationAdministration - used by serviceProvider
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));
		reservationAdministrationCapability.setState(ReservationState.PLANNED);

		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservation), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);

		reservationPerformer.finishReservation(reservation);
	}

	/**
	 * 
	 * @throws ResourceNotFoundException
	 * 
	 */
	private Set<IRootResource> generateSampleResources() throws InstantiationException, IllegalAccessException, URISyntaxException {
		IRootResource resource1 = new RootResource(RootResourceDescriptor.create(new Specification(Type.TSON),
				Arrays.asList(new Endpoint(new URI("http://localhost:8182")))));
		IRootResource resource2 = new RootResource(RootResourceDescriptor.create(new Specification(Type.TSON),
				Arrays.asList(new Endpoint(new URI("http://localhost:8182")))));

		Set<IRootResource> rootResources = new HashSet<IRootResource>();

		rootResources.add(resource1);
		rootResources.add(resource2);

		return rootResources;

	}

	class MockService implements IService {

		@Override
		public IResource getResource() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IServiceMetaData getMetadata() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	class DummyTrigger implements Trigger {

		@Override
		public Date getEndDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getStartDate() {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
