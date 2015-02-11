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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ReservationManagement.class)
public class CreateAndCancelReservationTest {

	private ReservationManagement	reservationCapability;

	IServiceExecutionScheduler		serviceExecutionScheduler;
	IServiceProvider				serviceProvider;

	IReservationAdministration		reservationAdministrationCapability;

	IReservationPerformer			mockedReservationPerformer;

	@Before
	public void prepareTest() throws SecurityException, NoSuchMethodException, Exception {

		// partial mock the reservation capability
		reservationCapability = PowerMockito.mock(ReservationManagement.class);
		PowerMockito.doCallRealMethod().when(reservationCapability).createReservation();
		PowerMockito.doCallRealMethod().when(reservationCapability)
				.planReservation(Mockito.any(ReservationResource.class), Mockito.anySet(), Mockito.any(Period.class));
		PowerMockito.doCallRealMethod().when(reservationCapability).cancelPlannedReservation(Mockito.any(ReservationResource.class));
		PowerMockito.doCallRealMethod().when(reservationCapability).getReservations();
		PowerMockito.doCallRealMethod().when(reservationCapability, "getCurrentDate");
		PowerMockito.doCallRealMethod().when(reservationCapability).activate();
		PowerMockito.doCallRealMethod().when(reservationCapability).getReservations(Mockito.any(ReservationState.class));

		// mock scheduler capability
		serviceExecutionScheduler = PowerMockito.mock(IServiceExecutionScheduler.class);
		ReflectionTestHelper.injectPrivateField(reservationCapability, serviceExecutionScheduler, "serviceExecutionScheduler");

		// inject and mock service provider
		serviceProvider = PowerMockito.mock(IServiceProvider.class);
		ReflectionTestHelper.injectPrivateField(reservationCapability, serviceProvider, "serviceProvider");
		Mockito.when(serviceProvider.getService(Mockito.any(IResource.class), Mockito.anyString(), Mockito.<Class> anyVararg())).thenReturn(
				new MockService());

		// inject resource
		IResource resource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));
		ReflectionTestHelper.injectPrivateField(reservationCapability, resource, "resource");

		// inject dummy performerCapability
		mockedReservationPerformer = new dummyReservationPerformer();
		ReflectionTestHelper.injectPrivateField(reservationCapability, mockedReservationPerformer, "reservationPerformer");

		reservationCapability.activate();

	}

	/**
	 * <p>
	 * Test checks the {@link IReservationCapability#createReservation(Reservation)} method. It tries to create a reservation with an unvalid period,
	 * which should launch an {@link IllegalArgumentException}
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void planReservationForThePast() throws Exception {

		Date startDate = new Date(System.currentTimeMillis() - 10000000L);
		Date endDate = new Date(System.currentTimeMillis() + 5000L);

		Set<IRootResource> rootresources = generateSampleResources();

		ReservationResource reservation = (ReservationResource) reservationCapability.createReservation();

		reservationCapability.planReservation(reservation, rootresources, new Period(startDate, endDate));
	}

	/**
	 * <p>
	 * Test checks the {@link IReservationCapability#createReservation(Reservation)} method. It tries to reserve unavaialble devices in a specific
	 * period, which should launch a {@link ResourceReservationException}
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test(expected = ResourceReservationException.class)
	public void reserveUnavailableDevices() throws Exception {

		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 5000L);

		Set<IRootResource> rootresources = generateSampleResources();

		ReservationResource reservation = (ReservationResource) reservationCapability.createReservation();

		// create custom ReservationAdministration - used by serviceProvider
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));

		PowerMockito.doThrow(new ResourceReservationException()).when(reservationCapability, "checkResourcesAreAvailable",
				Mockito.eq(reservation));

		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservation), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);

		reservationCapability.planReservation(reservation, rootresources, new Period(startDate, endDate));

	}

	/**
	 * <p>
	 * Test checks the {@link IReservationCapability#createReservation(Reservation)} method. Reservation should be scheduled, according to the
	 * reservation period. Test checks:
	 * <ul>
	 * <li>Reservation is created</li>
	 * <li>Reservation is in planned state.</li>
	 * <li>{@link IServiceExecutionScheduler} is called to schedule the reservation execution.</li>
	 * <li>{@link IServiceProvider} is called to get the service to be executed by the <code>IServiceExecutionScheduler</code></li>
	 * </ul>
	 * </p>
	 * 
	 * @throws IllegalAccessException
	 * 
	 * @throws CapabilityNotFoundException
	 * @throws URISyntaxException
	 * @throws InstantiationException
	 * @throws ApplicationActivationException
	 * @throws ResourceReservationException
	 * @throws ServiceNotFoundException
	 * @throws ServiceExecutionSchedulerException
	 * 
	 * @throws Exception
	 */
	@Test
	public void createReservationToBeScheduled() throws InstantiationException, IllegalAccessException, URISyntaxException,
			ApplicationActivationException, CapabilityNotFoundException, ResourceReservationException, ServiceNotFoundException,
			ServiceExecutionSchedulerException {

		// create period dates
		Date startDate = new Date(System.currentTimeMillis() + 100000L);
		Date endDate = new Date(System.currentTimeMillis() + 200000L);

		// create custom ReservationAdministration that will be "bound" to new reservation resource
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));

		// generate resources to be reserved
		Set<IRootResource> rootresources = generateSampleResources();

		// REAL METHOD - create reservation
		Assert.assertTrue(reservationCapability.getReservations().isEmpty());
		ReservationResource reservationResource = (ReservationResource) reservationCapability.createReservation();
		Assert.assertEquals(1, reservationCapability.getReservations().size());

		ReservationResource existingReservation = (ReservationResource) reservationCapability.getReservations().iterator().next();
		Assert.assertNotNull(existingReservation);

		// manually "inject" and initialize the ReservationAdministration capability for this new reservation
		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservationResource), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);
		ReflectionTestHelper.injectPrivateField(reservationAdministrationCapability, reservationResource, "resource");
		reservationAdministrationCapability.activate();

		// check it's in CREATED state
		Assert.assertEquals(ReservationState.CREATED, reservationAdministrationCapability.getState());

		// REAL METHOD - plan the reservation
		reservationCapability.planReservation(reservationResource, rootresources, new Period(startDate, endDate));

		// check reservation was scheduled and it's in PLANNED state
		Mockito.verify(serviceProvider, Mockito.times(1)).getService(Mockito.any(IResource.class), Mockito.eq("performReservation"),
				Mockito.<Class> anyVararg());

		Mockito.verify(serviceExecutionScheduler, Mockito.times(1)).schedule(Mockito.any(ServiceExecution.class));
		Assert.assertEquals(ReservationState.PLANNED, reservationAdministrationCapability.getState());

	}

	/**
	 * <p>
	 * Test checks the {@link IReservationCapability#createReservation(Reservation)} method. Reservation should be immediately executed, according to
	 * the reservation period. Test checks:
	 * <ul>
	 * <li>Reservation is created</li>
	 * <li>Reservation is in planned state.</li>
	 * <li>{@link IServiceExecutionScheduler} is called to schedule the reservation execution.</li>
	 * <li>{@link IServiceProvider} is called to get the service to be executed by the <code>IServiceExecutionScheduler</code></li>
	 * </ul>
	 * </p>
	 * 
	 * @throws CapabilityNotFoundException
	 * @throws URISyntaxException
	 * @throws InstantiationException
	 * @throws ApplicationActivationException
	 * 
	 * @throws Exception
	 */
	@Test
	public void createReservationImmediately() throws SecurityException, IllegalArgumentException, IllegalAccessException,
			ResourceReservationException, ServiceNotFoundException, ServiceExecutionSchedulerException, CapabilityNotFoundException,
			InstantiationException, URISyntaxException, ApplicationActivationException {

		// create period dates
		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 200000L);

		// create custom ReservationAdministration that will be "bound" to new reservation resource
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));

		// generate resources to be reserved
		Set<IRootResource> rootresources = generateSampleResources();

		// REAL METHOD - create reservation
		Assert.assertTrue(reservationCapability.getReservations().isEmpty());
		ReservationResource reservationResource = (ReservationResource) reservationCapability.createReservation();
		Assert.assertEquals(1, reservationCapability.getReservations().size());

		ReservationResource existingReservation = (ReservationResource) reservationCapability.getReservations().iterator().next();
		Assert.assertNotNull(existingReservation);

		// manually "inject" and initialize the ReservationAdministration capability for this new reservation
		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservationResource), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);
		ReflectionTestHelper.injectPrivateField(reservationAdministrationCapability, reservationResource, "resource");
		reservationAdministrationCapability.activate();

		// check it's in CREATED state
		Assert.assertEquals(ReservationState.CREATED, reservationAdministrationCapability.getState());

		// REAL METHOD - plan the reservation
		reservationCapability.planReservation(reservationResource, rootresources, new Period(startDate, endDate));

		// check reservation was not scheduled and it's in RESERVED state

		Mockito.verify(serviceProvider, Mockito.times(0)).getService(Mockito.any(IResource.class), Mockito.eq("performReservation"),
				Mockito.<Class> anyVararg());

		Mockito.verify(serviceExecutionScheduler, Mockito.times(0)).schedule(Mockito.any(ServiceExecution.class));
		Assert.assertEquals(ReservationState.RESERVED, reservationAdministrationCapability.getState());

	}

	/**
	 * <p>
	 * Test checks the {@link IReservationCapability#cancelReservation(Reservation)} method. Test tries to cancel an unexisting reservation, which
	 * should fails with a {@link ResourceReservationException}
	 * </p>
	 * 
	 * @throws Exception
	 */
	@Test(expected = ResourceReservationException.class)
	public void cancelUnexistingReservation() throws SecurityException, IllegalArgumentException, IllegalAccessException,
			ResourceReservationException {

		ReservationResource notExistingReservation = new ReservationResource();

		reservationCapability.cancelPlannedReservation(notExistingReservation);
	}

	/**
	 * <p>
	 * Test checks the {@link IReservationCapability#cancelReservation(Reservation)} method. Test cancels a scheduled reservation, so it checks:
	 * <ul>
	 * <li>The reservation exists before executing the method</li>
	 * <li>The reservation does not exist after executing the method</li>
	 * <li>The {@link IServiceExecutionScheduler} has been called in order to cancel the scheduled reservation</li>
	 * 
	 * </ul>
	 * </p>
	 * 
	 * @throws URISyntaxException
	 * @throws InstantiationException
	 * @throws CapabilityNotFoundException
	 * 
	 * @throws Exception
	 */
	@Test
	public void cancelPlannedReservation1() throws SecurityException, IllegalArgumentException, IllegalAccessException,
			ResourceReservationException, ServiceExecutionSchedulerException, InstantiationException, URISyntaxException, CapabilityNotFoundException {

		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 200000L);

		// create custom ReservationAdministration that will be "bound" to new reservation resource
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));
		reservationAdministrationCapability.setState(ReservationState.PLANNED);

		// manually create a reservation
		ReservationResource reservation = new ReservationResource();

		// initialize reservations with generated one
		Set<ReservationResource> reservations = new HashSet<ReservationResource>();
		reservations.add(reservation);
		ReflectionTestHelper.injectPrivateField(reservationCapability, reservations, "reservations");

		Map<ReservationResource, ServiceExecution> reservationsServicesExeceutions = new ConcurrentHashMap<ReservationResource, ServiceExecution>();
		ServiceExecution se = new ServiceExecution(new MockService(), new dummyTrigger());
		reservationsServicesExeceutions.put(reservation, se);
		ReflectionTestHelper.injectPrivateField(reservationCapability, reservationsServicesExeceutions, "reservationsServicesExeceutions");

		// check we injected it correctly ;)
		Assert.assertEquals(1, reservationCapability.getReservations().size());

		// manually "inject" and initialize the ReservationAdministration capability for this new reservation
		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservation), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);
		ReflectionTestHelper.injectPrivateField(reservationAdministrationCapability, reservation, "resource");

		// REAL METHOD - call planned reservation
		Assert.assertTrue(reservationCapability.getReservations(ReservationState.FINISHED).isEmpty());
		reservationCapability.cancelPlannedReservation(reservation);

		// check it was canceled
		Assert.assertFalse(reservationCapability.getReservations().isEmpty());
		Mockito.verify(serviceExecutionScheduler, Mockito.times(1)).cancel(Mockito.eq(se));
		Assert.assertFalse(reservationCapability.getReservations(ReservationState.FINISHED).isEmpty());
		Assert.assertEquals(ReservationState.FINISHED, reservationAdministrationCapability.getState());

	}

	/**
	 * <p>
	 * Test checks the {@link IReservationCapability#cancelReservation(Reservation)} method. Test cancels a planned reservation, so it checks:
	 * <ul>
	 * <li>The reservation exists before executing the method</li>
	 * <li>The reservation does not exist after executing the method</li>
	 * <li>The {@link IServiceExecutionScheduler} has not been called</li>
	 * </ul>
	 * Take into account that the test does not check if the reservation has been removed, since it's done by the
	 * {@link IReservationCapability#releaseReservation(Reservation)} method (and it's mocked!)
	 * </p>
	 * 
	 * @throws CapabilityNotFoundException
	 * @throws URISyntaxException
	 * @throws InstantiationException
	 * 
	 * @throws Exception
	 */
	@Test
	public void cancelPerformedReservation() throws SecurityException, IllegalArgumentException, IllegalAccessException,
			ResourceReservationException, ServiceExecutionSchedulerException, CapabilityNotFoundException, InstantiationException, URISyntaxException {

		Date startDate = new Date(System.currentTimeMillis());
		Date endDate = new Date(System.currentTimeMillis() + 200000L);

		// create custom ReservationAdministration that will be "bound" to new reservation resource
		reservationAdministrationCapability = new ReservationAdministration();
		reservationAdministrationCapability.setResources(generateSampleResources());
		reservationAdministrationCapability.setPeriod(new Period(startDate, endDate));
		reservationAdministrationCapability.setState(ReservationState.RESERVED);

		// manually create a reservation
		ReservationResource reservation = new ReservationResource();

		// initialize reservations with generated one
		Set<ReservationResource> reservations = new HashSet<ReservationResource>();
		reservations.add(reservation);
		ReflectionTestHelper.injectPrivateField(reservationCapability, reservations, "reservations");

		Map<ReservationResource, ServiceExecution> reservationsServicesExeceutions = new ConcurrentHashMap<ReservationResource, ServiceExecution>();
		ServiceExecution se = new ServiceExecution(new MockService(), new dummyTrigger());
		reservationsServicesExeceutions.put(reservation, se);
		ReflectionTestHelper.injectPrivateField(reservationCapability, reservationsServicesExeceutions, "reservationsServicesExeceutions");

		// check we injected it correctly ;)
		Assert.assertEquals(1, reservationCapability.getReservations().size());

		// manually "inject" and initialize the ReservationAdministration capability for this new reservation
		Mockito.when(serviceProvider.getCapability(Mockito.eq(reservation), Mockito.eq(IReservationAdministration.class))).thenReturn(
				reservationAdministrationCapability);
		ReflectionTestHelper.injectPrivateField(reservationAdministrationCapability, reservation, "resource");

		// REAL METHOD - call planned reservation
		reservationCapability.cancelPlannedReservation(reservation);

		// check it was canceled
		Assert.assertFalse(reservationCapability.getReservations().isEmpty());
		Assert.assertFalse(reservationCapability.getReservations(ReservationState.FINISHED).isEmpty());
		Assert.assertEquals(ReservationState.FINISHED, reservationAdministrationCapability.getState());

		Mockito.verify(serviceExecutionScheduler, Mockito.times(0)).cancel(Mockito.eq(se));

	}

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

	class dummyReservationPerformer implements IReservationPerformer {

		@Override
		public void performReservation(ReservationResource reservation) throws ResourceReservationException {
			try {
				serviceProvider.getCapability(reservation, IReservationAdministration.class).setState(ReservationState.RESERVED);
			} catch (CapabilityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void cancelReservation(ReservationResource reservation) throws ResourceReservationException {
			try {
				serviceProvider.getCapability(reservation, IReservationAdministration.class).setState(ReservationState.FINISHED);
			} catch (CapabilityNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void activate() throws ApplicationActivationException {
			// TODO Auto-generated method stub

		}

		@Override
		public void deactivate() {
			// TODO Auto-generated method stub

		}

	}

	class dummyTrigger implements Trigger {

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
