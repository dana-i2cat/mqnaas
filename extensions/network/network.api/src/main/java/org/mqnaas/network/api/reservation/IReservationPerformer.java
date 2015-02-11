package org.mqnaas.network.api.reservation;

import org.mqnaas.core.api.ICapability;

/**
 * 
 * <p>
 * Capability performing resources reservations.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface IReservationPerformer extends ICapability {

	void performReservation(ReservationResource reservation) throws ResourceReservationException;

	void cancelReservation(ReservationResource reservation) throws ResourceReservationException;

	void finishReservation(ReservationResource reservation) throws ResourceReservationException;

}
