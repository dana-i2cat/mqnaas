package org.mqnaas.network.api.reservation;

/**
 * 
 * <p>
 * Capability performing resources reservations.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface IReservationPerformer {

	void performReservation(ReservationResource reservation);

	void cancelReservation(ReservationResource reservation);

}
