package org.mqnaas.network.api.reservation;

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;

/**
 * <p>
 * Management capability for {@link ReservationResource} resources.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface IReservationManagement extends ICapability {

	@AddsResource
	IResource createReservation();

	@RemovesResource
	void removeReservation(IResource reservation);

	@ListsResources
	List<IResource> getReservations();

}
