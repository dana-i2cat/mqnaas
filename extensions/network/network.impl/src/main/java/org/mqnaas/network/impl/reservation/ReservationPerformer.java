package org.mqnaas.network.impl.reservation;

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
import org.mqnaas.network.api.reservation.IReservationPerformer;
import org.mqnaas.network.api.reservation.ReservationResource;
import org.mqnaas.network.api.reservation.ResourceReservationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ReservationPerformer implements IReservationPerformer {

	private static final Logger					log	= LoggerFactory.getLogger(ReservationPerformer.class);

	@DependingOn
	IServiceProvider							serviceProvider;

	@DependingOn
	IServiceExecutionScheduler					serviceExecutionScheduler;

	@Resource
	private IResource							resource;

	/**
	 * Stores relationship between a performed reservation and the {@link ServiceExecution} instance used to schedule its cancellation.
	 * 
	 * @see #performReservation(ReservationResource)
	 */
	Map<ReservationResource, ServiceExecution>	scheduledFinishReservationServicesExecutions;

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing ReservationPerformer capability for resource " + resource.getId());
		scheduledFinishReservationServicesExecutions = new ConcurrentHashMap<ReservationResource, ServiceExecution>();
		log.info("Initialized ReservationPerformer capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing ReservationPerformer capability from resource " + resource.getId());

		log.info("Removing ReservationPerformer capability from resource " + resource.getId());
	}

	@Override
	public void performReservation(ReservationResource reservation) throws ResourceReservationException {
		if (reservation == null)
			throw new NullPointerException("Can not perform a null reservation.");

		log.info("Performing reservation [id=" + reservation.getId() + "]");
		IReservationAdministration reservationAdminCapab;

		try {
			reservationAdminCapab = serviceProvider.getCapability(reservation, IReservationAdministration.class);

		} catch (CapabilityNotFoundException c) {
			throw new ResourceReservationException("Could not perform reservation [id=" + reservation.getId() + "]", c);
		}

		Period period = reservationAdminCapab.getPeriod();
		Set<IRootResource> resources = reservationAdminCapab.getResources();
		ReservationState state = reservationAdminCapab.getState();

		if (period == null || resources == null)
			throw new NullPointerException("Can only perform reservations with valid period and resources.");

		if (period.getEndDate() == null)
			throw new NullPointerException("A resource reservation requires an end date to be performed.");

		if (resources.isEmpty())
			throw new IllegalArgumentException("At least one resource is required to perform a reservation.");

		if (!state.equals(ReservationState.PLANNED))
			throw new IllegalStateException("Can only perform reservations on PLANNED reservations.");

		try {

			log.debug("Scheduling end of reservation [id=" + reservation.getId() + ", resources=" + resources + ", endDate=" + period.getEndDate() + "]");

			IService service = serviceProvider.getService(resource, "cancelReservation", ReservationResource.class);
			Trigger trigger = TriggerFactory.create(period.getEndDate());
			ServiceExecution serviceExecution = new ServiceExecution(service, trigger);
			serviceExecutionScheduler.schedule(serviceExecution);

			scheduledFinishReservationServicesExecutions.put(reservation, serviceExecution);

			log.debug("Scheduled end of reservation [id=" + reservation.getId() + "]");

		} catch (ServiceExecutionSchedulerException e) {
			log.warn("Could not schedule ReleaseReservation service.", e);
		} catch (ServiceNotFoundException e) {
			log.warn("Could not schedule ReleaseReservation service.", e);
		}

		reservationAdminCapab.setState(ReservationState.RESERVED);

		log.info("Perfomed reservation [id=" + reservation.getId() + "]");

	}

	@Override
	public void cancelReservation(ReservationResource reservation) throws ResourceReservationException {
		if (reservation == null)
			throw new NullPointerException("Can not cancel a null reservation.");

		log.info("Cancelling performed reservation [id=" + reservation.getId() + "]");
		IReservationAdministration reservationAdminCapab;

		try {
			reservationAdminCapab = serviceProvider.getCapability(reservation, IReservationAdministration.class);

		} catch (CapabilityNotFoundException c) {
			throw new ResourceReservationException("Could not cancel performed reservation [id=" + reservation.getId() + "]", c);
		}

		ReservationState state = reservationAdminCapab.getState();

		if (!state.equals(ReservationState.RESERVED))
			throw new IllegalStateException("Can only cancel reservations on RESERVED state.");

		try {
			serviceExecutionScheduler.cancel(scheduledFinishReservationServicesExecutions.get(reservation));
		} catch (ServiceExecutionSchedulerException e) {
			throw new ResourceReservationException("Could not cancel reservation.", e);
		}

		scheduledFinishReservationServicesExecutions.remove(reservation);
		reservationAdminCapab.setState(ReservationState.FINISHED);

		log.info("Cancelled performed reservation [id=" + reservation.getId() + "]");

	}
}
