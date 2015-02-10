package org.mqnaas.network.api.reservation;

import java.util.Set;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.network.api.request.Period;

/**
 * <p>
 * Capability managing resources reservation planning.
 * </p>
 * <p>
 * This capability allows to reserve OpenNaaS {@link IRootResource}s for a specific {@link Period} of time. Reservations for a specific resource are
 * only allowed if the resource is available in this period of time, i.e. it does not exist any other reservation containing this resource during the
 * same time.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface IReservationPlanner extends ICapability {

	void planReservation(ReservationResource reservation, Set<IRootResource> resources, Period period);

	void cancelPlannedReservation(ReservationResource reservation);

}
