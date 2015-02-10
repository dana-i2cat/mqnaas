package org.mqnaas.network.impl.reservation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
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
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ReservationManagement implements IReservationManagement, IReservationPlanner {

	private static final Logger							log	= LoggerFactory.getLogger(ReservationManagement.class);

	private Map<ReservationResource, ServiceExecution>	reservations;

	@Resource
	IResource											resource;

	@DependingOn
	IReservationPerformer								reservationPerformer;

	@DependingOn
	IServiceProvider									serviceProvider;

	@DependingOn
	IServiceExecutionScheduler							serviceExecutionScheduler;

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing ReservationManagement for resource " + resource.getId());

		reservations = new ConcurrentHashMap<ReservationResource, ServiceExecution>();

		log.info("Initialized ReservationManagement for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing ReservationManagement from resource " + resource.getId());

		// TODO cancel all reservations

		log.info("Removed ReservationManagement from resource " + resource.getId());

	}

	@Override
	public void planReservation(ReservationResource reservation, Set<IRootResource> resources, Period period) throws ResourceReservationException {

		if (reservation == null || period == null || resources == null)
			throw new NullPointerException("Reservation, resources and period are required to plan a reservation.");

		if (resources.isEmpty())
			throw new IllegalArgumentException("You need at least one resource in order to plan a reservation");

		log.info("Planning reservation [ " + reservation.getId() + "]");

		ServiceExecution serviceExecution = null;
		Date currentDate = getCurrentDate();

		if (period.getStartdate().before(currentDate))
			throw new IllegalArgumentException("The reservation period should be a future period.");

		checkResourcesAreAvailable(reservation);

		try {
			IReservationAdministration reservationAdmin = serviceProvider.getCapability(reservation, IReservationAdministration.class);
			reservationAdmin.setState(ReservationState.PLANNED);

			if (reservationAdmin.getPeriod().getStartdate().after(new Date(System.currentTimeMillis()))) {
				IService service = serviceProvider.getService(resource, "performReservation", ReservationResource.class);

				Trigger trigger = TriggerFactory.create(reservationAdmin.getPeriod().getStartdate());
				serviceExecution = new ServiceExecution(service, trigger);

				reservations.put(reservation, serviceExecution);

				serviceExecutionScheduler.schedule(serviceExecution);

			}
			else {
				reservations.put(reservation, serviceExecution);
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

	private void checkResourcesAreAvailable(ReservationResource reservation) throws ResourceReservationException {
		try {
			IReservationAdministration reservationAdmin = serviceProvider.getCapability(reservation, IReservationAdministration.class);

			for (ReservationResource existingReservation : reservations.keySet()) {

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

	@Override
	public void cancelPlannedReservation(ReservationResource reservation) throws ResourceReservationException {
		if (reservation == null)
			throw new NullPointerException("Reservation is required to cancel a reservation.");

		if (!reservations.containsKey(reservation))
			throw new ResourceReservationException("Could not cancel reservation: Reservation does not exists.");

		log.info("Cancelling Reservation [" + reservation.getId() + "]");

		try {
			IReservationAdministration reservationAdmin = serviceProvider.getCapability(reservation, IReservationAdministration.class);

			if (reservationAdmin.getState().equals(ReservationState.PLANNED))

				try {
					serviceExecutionScheduler.cancel(reservations.get(reservation));

					reservations.remove(reservation);

				} catch (ServiceExecutionSchedulerException e) {
					throw new ResourceReservationException(e);
				}
			else
				reservationPerformer.cancelReservation(reservation);

		} catch (CapabilityNotFoundException c) {
			throw new ResourceReservationException("Could not cancel reservation [" + reservation.getId() + "]", c);

		}
		log.info("Devices reservation cancelled.");

	}

	@Override
	public IResource createReservation() {

		ReservationResource reservationResource = new ReservationResource();
		reservations.put(reservationResource, null);
		return reservationResource;
	}

	@Override
	public void removeReservation(IResource reservation) {

		if (reservation == null || !(reservation instanceof ReservationResource))
			throw new IllegalArgumentException("Can only remove reservations resources.");

		if (!reservations.containsKey(reservation))
			throw new IllegalStateException("Didn't find reservation [id=" + reservation.getId() + "]. Can only remove existing reservations.");

		IReservationAdministration reservationAdmin;
		try {
			reservationAdmin = serviceProvider.getCapability(reservation, IReservationAdministration.class);
		} catch (CapabilityNotFoundException e) {
			throw new IllegalStateException(
					"Could not remove reservation[ " + reservation.getId() + "]. Could not get its IReservationAdministration capability.", e);
		}

		if (reservationAdmin.getState() != ReservationState.CREATED)
			throw new IllegalStateException(
					"Can only remove not planned nor performed reservations. Please release the reservation first. [ " + reservation
							.getId() + ",state=" + reservationAdmin.getState() + "]");

		reservations.remove(reservation);
	}

	@Override
	public List<IResource> getReservations() {
		return new ArrayList<IResource>(reservations.keySet());
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

}
