package org.mqnaas.network.impl.reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.reservation.IReservationAdministration;
import org.mqnaas.network.api.reservation.IReservationAdministration.ReservationState;
import org.mqnaas.network.api.reservation.IReservationManagement;
import org.mqnaas.network.api.reservation.IReservationPerformer;
import org.mqnaas.network.api.reservation.IReservationPlanner;
import org.mqnaas.network.api.reservation.ReservationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ReservationManagement implements IReservationManagement, IReservationPlanner {

	private static final Logger			log	= LoggerFactory.getLogger(ReservationManagement.class);

	private List<ReservationResource>	reservations;

	@Resource
	IResource							resource;

	@DependingOn
	IReservationPerformer				reservationPerformer;

	@DependingOn
	IServiceProvider					serviceProvider;

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing ReservationManagement for resource " + resource.getId());

		reservations = new CopyOnWriteArrayList<ReservationResource>();

		log.info("Initialized ReservationManagement for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing ReservationManagement from resource " + resource.getId());

		// TODO cancel all reservations

		log.info("Removed ReservationManagement from resource " + resource.getId());

	}

	@Override
	public void planReservation(ReservationResource reservation, Set<IRootResource> resources, Period period) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelPlannedReservation(ReservationResource reservation) {
		// TODO Auto-generated method stub

	}

	@Override
	public IResource createReservation() {

		ReservationResource reservationResource = new ReservationResource();
		reservations.add(reservationResource);
		return reservationResource;
	}

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

		if (reservationAdmin.getState() != ReservationState.CREATED)
			throw new IllegalStateException(
					"Can only remove not planned nor performed reservations. Please release the reservation first. [ " + reservation
							.getId() + ",state=" + reservationAdmin.getState() + "]");

		reservations.remove(reservation);
	}

	@Override
	public List<IResource> getReservations() {
		return new ArrayList<IResource>(reservations);
	}

}
