package org.mqnaas.network.impl.reservation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.i2cat.dana.mqnaas.capability.reservation.IReservationCapability;
import net.i2cat.dana.mqnaas.capability.reservation.model.Reservation;
import net.i2cat.dana.nitos.reservation.INitosReservationCapability;
import net.i2cat.dana.nitos.reservation.exception.NitosReservationException;

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
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.reservation.IReservationAdministration;
import org.mqnaas.network.api.reservation.IReservationAdministration.ReservationState;
import org.mqnaas.network.api.reservation.IReservationPerformer;
import org.mqnaas.network.api.reservation.ReservationResource;
import org.mqnaas.network.api.reservation.ResourceReservationException;
import org.powermock.api.mockito.PowerMockito;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class PerformAndReleaseReservationTest {

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
	 * Test checks the {@link IReservationCapability#performReservation(Reservation)} method. It performs the reservation of three devices: two
	 * belongs to NITOS, and the other one is a MqNaaS existing resource. Therefore, {@link INitosReservationCapability} and
	 * {@link IRootResourceManagement} have to me mocked.
	 * </p>
	 * <p>
	 * Asserts checks that:
	 * <ul>
	 * <li>{@link INitosReservationCapability#reserveDevices(Set, Date, Date)} only with NITOS devices and the correct dates.</li>
	 * <li>{@link IRootResourceManagement#createRootResource(Specification, java.util.Collection)} is called for each NITOS reserved device.</li>
	 * <li>After the service execution, the {@link Reservation} is in the {@link ReservationState#RESERVED} state</li>
	 * <li>The <code>Reservation</code> contains so many resources as reserved devices.</li>
	 * </ul>
	 * </p>
	 * 
	 * @throws ServiceExecutionSchedulerException
	 * @throws NitosReservationException
	 * @throws URISyntaxException
	 * @throws CapabilityNotFoundException
	 */
	@Test
	public void performReservationTest() throws ResourceReservationException, SecurityException, IllegalArgumentException, IllegalAccessException,
			ServiceExecutionSchedulerException, NitosReservationException, InstantiationException, URISyntaxException, CapabilityNotFoundException {

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
	 * Test checks the {@link IReservationCapability#performReservation(Reservation)} method. It tries to perform a reservation of an already
	 * performed reservation, which should fail with a {@link ResourceReservationException}.
	 * </p>
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws CapabilityNotFoundException
	 * @throws URISyntaxException
	 * @throws InstantiationException
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
	 * Test checks the {@link IReservationCapability#releaseReservation(Reservation)} method. It releases the reservation of three devices: two
	 * belongs to NITOS, and the other one is a MqNaaS resource. Therefore, {@link INitosReservationCapability} and {@link IRootResourceManagement}
	 * have to me mocked.
	 * </p>
	 * <p>
	 * Asserts checks that:
	 * <ul>
	 * <li>{@link INitosReservationCapability#releaseDevices(String)} is called only once</li>
	 * <li>{@link IRootResourceManagement#removeRootResource(IRootResource)} is called for each NITOS reserved device.</li>
	 * <li>At the end of the method, the <code>Reservation</code> does not longer exists</li>
	 * </ul>
	 * </p>
	 * 
	 * @throws CapabilityNotFoundException
	 * @throws URISyntaxException
	 * @throws ServiceExecutionSchedulerException
	 * 
	 */
	@Test
	public void releaseReservationTest() throws ResourceReservationException, SecurityException, IllegalArgumentException, IllegalAccessException,
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

		Assert.assertEquals(ReservationState.FINISHED, reservationAdministrationCapability.getState());
		Assert.assertFalse((((ReservationPerformer) reservationPerformer).scheduledFinishReservationServicesExecutions).containsKey(reservation));
		Mockito.verify(serviceExecutionScheduler, Mockito.times(1)).cancel(Mockito.any(ServiceExecution.class));

	}

	/**
	 * <p>
	 * Test checks the {@link IReservationCapability#releaseReservation(Reservation)} method. It tries to release still-not-reserved reservation,
	 * which should fail with a {@link ResourceReservationException}.
	 * </p>
	 * 
	 * @throws ResourceReservationException
	 * @throws URISyntaxException
	 * @throws InstantiationException
	 * 
	 * @throws IllegalAccessException
	 * @throws CapabilityNotFoundException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
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
