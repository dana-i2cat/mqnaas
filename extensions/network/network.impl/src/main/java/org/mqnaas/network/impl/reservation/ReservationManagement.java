package org.mqnaas.network.impl.reservation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ServiceExecutionSchedulerException;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.api.scheduling.IServiceExecutionScheduler;
import org.mqnaas.core.api.scheduling.ServiceExecution;
import org.mqnaas.core.api.scheduling.Trigger;
import org.mqnaas.core.impl.scheduling.TriggerFactory;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.reservation.IReservationAdministration;
import org.mqnaas.network.api.reservation.IReservationAdministration.ReservationState;
import org.mqnaas.network.api.reservation.IReservationManagement;
import org.mqnaas.network.api.reservation.IReservationPerformer;
import org.mqnaas.network.api.reservation.IReservationPlanner;
import org.mqnaas.network.api.reservation.ReservationResource;
import org.mqnaas.network.api.reservation.ResourceReservationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Generic capability implementation for {@link IReservationManagement} and {@link IReservationPlanner} capabilities.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ReservationManagement implements IReservationManagement, IReservationPlanner {

	private static final Logger							log	= LoggerFactory.getLogger(ReservationManagement.class);

	/**
	 * Stores relationship between a planned reservation and the {@link ServiceExecution} instance used to schedule it.
	 */
	private Map<ReservationResource, ServiceExecution>	reservationsServicesExeceutions;

	private Set<ReservationResource>					reservations;

	@Resource
	IResource											resource;

	@DependingOn
	IReservationPerformer								reservationPerformer;

	@DependingOn
	IServiceProvider									serviceProvider;

	@DependingOn
	IServiceExecutionScheduler							serviceExecutionScheduler;

	public static boolean isSupporting(IRootResource rootResource) {
		Type type = rootResource.getDescriptor().getSpecification().getType();

		return type == Type.NETWORK || type == Type.CORE;
	}

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing ReservationManagement for resource " + resource.getId());

		reservations = new HashSet<ReservationResource>();
		reservationsServicesExeceutions = new ConcurrentHashMap<ReservationResource, ServiceExecution>();

		log.info("Initialized ReservationManagement for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing ReservationManagement from resource " + resource.getId());

		// TODO cancel all reservations

		log.info("Removed ReservationManagement from resource " + resource.getId());

	}

	/**
	 * <p>
	 * Plans a reservation of resources during a specific period of time.
	 * </p>
	 * <p>
	 * Methods checks If given <code>resources</code> are available during the desired <code>period</code>. If so, the reservation is either
	 * Immediately performed or it's scheduled to be done performed in the future, depending on the value of the {@link Period#getStartdate()}
	 * </p>
	 * 
	 * @param reservation
	 *            {@link ReservationResource} to be planned.
	 * @param resources
	 *            Set of {@link IRootResource}s to be reserved.
	 * @param period
	 *            Reservation's {@link Period}
	 * @throws ResourceReservationException
	 *             If given <code>resources</code> are not available during the specified <code>period</code>
	 */
	@Override
	public void planReservation(ReservationResource reservation) throws ResourceReservationException {

		if (reservation == null)
			throw new NullPointerException("Reservation, resources and period are required to plan a reservation.");

		IReservationAdministration reservationAdmin;

		try {
			reservationAdmin = serviceProvider.getCapability(reservation, IReservationAdministration.class);
		} catch (CapabilityNotFoundException c) {
			throw new ResourceReservationException(c);
		}

		Set<IRootResource> resources = reservationAdmin.getResources();
		Period period = reservationAdmin.getPeriod();

		if (resources.isEmpty())
			throw new IllegalArgumentException("You need at least one resource in order to plan a reservation");

		log.info("Planning reservation [ " + reservation.getId() + "]");

		Date currentDate = getCurrentDate();

		if (period.getStartdate().before(currentDate))
			throw new IllegalArgumentException("The reservation period should be a future period.");

		checkResourcesAreAvailable(reservation);

		try {
			reservationAdmin = serviceProvider.getCapability(reservation, IReservationAdministration.class);
			reservationAdmin.setState(ReservationState.PLANNED);

			if (reservationAdmin.getPeriod().getStartdate().after(new Date(System.currentTimeMillis()))) {
				IService service = serviceProvider.getService(resource, "performReservation", ReservationResource.class);

				Trigger trigger = TriggerFactory.create(reservationAdmin.getPeriod().getStartdate());
				ServiceExecution serviceExecution = new ServiceExecution(service, trigger);

				reservationsServicesExeceutions.put(reservation, serviceExecution);

				serviceExecutionScheduler.schedule(serviceExecution);

			}
			else {
				reservationPerformer.performReservation(reservation);
			}
		} catch (CapabilityNotFoundException c) {
			throw new ResourceReservationException(c);
		} catch (ServiceNotFoundException e) {
			log.error("Could not obtain performReservation service.", e);
			throw new ResourceReservationException(e);
		} catch (ServiceExecutionSchedulerException e) {
			throw new ResourceReservationException(e);
		}

	}

	/**
	 * <p>
	 * Cancels an already planned reservation.
	 * </p>
	 * <p>
	 * If the reservation is in {@link ReservationState#PLANNED PLANNED} state, it cancels the scheduled service so it's not performed. If the
	 * reservation is in {@link ReservationState#RESERVED RESERVED} state (which mean it was already performed), it releases all the
	 * {@link IRootResource}s of the involved reservation.
	 * </p>
	 */
	@Override
	public void cancelPlannedReservation(ReservationResource reservation) throws ResourceReservationException {
		if (reservation == null)
			throw new NullPointerException("Reservation is required to cancel a reservation.");

		if (!reservations.contains(reservation))
			throw new ResourceReservationException("Could not cancel reservation: Reservation does not exists.");

		log.info("Cancelling Reservation [" + reservation.getId() + "]");

		try {
			IReservationAdministration reservationAdmin = serviceProvider.getCapability(reservation, IReservationAdministration.class);

			if (reservationAdmin.getState().equals(ReservationState.PLANNED))

				try {
					serviceExecutionScheduler.cancel(reservationsServicesExeceutions.get(reservation));

					reservationsServicesExeceutions.remove(reservation);

					reservationAdmin.setState(ReservationState.CANCELLED);

				} catch (ServiceExecutionSchedulerException e) {
					throw new ResourceReservationException(e);
				}
			else
				reservationPerformer.cancelReservation(reservation);

		} catch (CapabilityNotFoundException c) {
			throw new ResourceReservationException("Could not cancel reservation [" + reservation.getId() + "]", c);

		}

		log.info("Cancelled Reservation [" + reservation.getId() + "]");

	}

	/**
	 * <p>
	 * Creates a {@link ReservationResource} instance.
	 * </p>
	 */
	@Override
	public IResource createReservation() {

		ReservationResource reservationResource = new ReservationResource();
		reservations.add(reservationResource);
		return reservationResource;
	}

	/**
	 * <p>
	 * Removes a {@link ReservationResource} instance. Only not performed nor planned reservations can be removed. Planned and reserved reservations
	 * must be canceled first using {@link #cancelPlannedReservation(ReservationResource)} method.
	 * </p>
	 */
	@Override
	public void removeReservation(IResource reservation) {

		if (reservation == null || !(reservation instanceof ReservationResource))
			throw new IllegalArgumentException("Can only remove reservations resources.");

		if (!reservations.contains(reservation))
			throw new IllegalStateException("Didn't find reservation [id=" + reservation.getId() + "]. Can only remove existing reservations.");

		IReservationAdministration reservationAdmin;
		try {
			reservationAdmin = serviceProvider.getCapability(reservation, IReservationAdministration.class);
		} catch (CapabilityNotFoundException e) {
			throw new IllegalStateException(
					"Could not remove reservation[ " + reservation.getId() + "]. Could not get its IReservationAdministration capability.", e);
		}

		if (reservationAdmin.getState() == ReservationState.PLANNED || reservationAdmin.getState() != ReservationState.RESERVED)
			throw new IllegalStateException(
					"Can only remove not planned nor performed reservations. Please release the reservation first. [ " + reservation
							.getId() + ",state=" + reservationAdmin.getState() + "]");

		reservations.remove(reservation);
	}

	@Override
	public List<IResource> getReservations() {
		return new ArrayList<IResource>(reservations);
	}

	@Override
	public List<IResource> getReservations(ReservationState state) {

		if (state == null)
			return getReservations();

		List<IResource> reservations = new ArrayList<IResource>();
		for (IResource reservation : getReservations()) {
			try {
				if (serviceProvider.getCapability(reservation, IReservationAdministration.class).getState() == state)
					reservations.add(reservation);
			} catch (CapabilityNotFoundException c) {
				log.warn("Could not get state of reservation [" + reservation.getId() + "]. Ignoring it.");
			}
		}

		return reservations;
	}

	/**
	 * Returns current date, decrementing 1 hour.
	 * 
	 * @return {@link Date} specifying 1 hour before the current date.
	 */
	private Date getCurrentDate() {

		long currentTime = System.currentTimeMillis();

		return new Date(currentTime - (1000 * 60 * 60));

	}

	/**
	 * Checks if the {@link IRootResource}s of the given <code>reservation</code> are available in the reservation's period of time, or if they're
	 * already reserved by another existing reservation.
	 * 
	 * @param reservation
	 *            {@link ReservationResource} containing a set of {@link IRootResource}s to be reverved in a specific {@link Period} of time.
	 * @throws ResourceReservationException
	 *             If resources are not available of if the given <code>reservation</code> does not have {@link IReservationAdministration} capability
	 *             for managing {@link IRootResource}s and {@link Period} information.
	 */
	private void checkResourcesAreAvailable(ReservationResource reservation) throws ResourceReservationException {
		try {
			IReservationAdministration reservationAdmin = serviceProvider.getCapability(reservation, IReservationAdministration.class);

			for (ReservationResource existingReservation : reservations) {

				// do not compare with current reservation, since it's stored in the map as well.
				if (existingReservation.equals(reservation))
					continue;

				IReservationAdministration existingReservationAdmin = serviceProvider.getCapability(existingReservation,
						IReservationAdministration.class);

				if (!ReservationUtils.areResourcesAvailable(existingReservationAdmin, reservationAdmin))
					throw new ResourceReservationException("Resource are not available during the requested period.");

			}
		} catch (CapabilityNotFoundException c) {
			throw new ResourceReservationException("Could not plan reservation [" + reservation.getId() + "]", c);

		}

	}

}
