package org.mqnaas.network.api.reservation;

import java.util.Set;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.network.api.request.Period;

/**
 * <p>
 * Capability managing the {@link IRootResource}s, {@link Period} and state of a {@link ReservationResource}
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface IReservationAdministration extends ICapability {

	public enum ReservationState {
		CREATED,
		PLANNED,
		RESERVED,
		CANCELLED,
		FINISHED
	}

	void setResources(Set<IRootResource> resources);

	void setPeriod(Period period);

	void setState(ReservationState state);

	Set<IRootResource> getResources();

	Period getPeriod();

	ReservationState getState();

}
