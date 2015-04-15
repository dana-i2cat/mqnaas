package org.mqnaas.network.impl.reservation;

/*
 * #%L
 * MQNaaS :: Network Implementation
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.mqnaas.core.api.IRootResource;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.reservation.IReservationAdministration;
import org.mqnaas.network.api.reservation.IReservationAdministration.ReservationState;
import org.mqnaas.network.api.reservation.ReservationResource;

/**
 * Utilities for the reservation capability.
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ReservationUtils {

	/**
	 * Checks if any {@link IRootResource} stored in the {@link IReservationAdministration} of a new {@link ReservationResource} are contained in the
	 * <code>existingReservationAdmin</code> for the same {@link Period}.
	 * 
	 * @param existingReservation
	 *            <code>IReservationAdministration</code> capability of an existing {@link ReservationResource}, containing its resources and period.
	 * @param reservation
	 *            <code>IReservationAdministration</code> capability of the new reservation to be checked.
	 * @return <code>true</code> If the resources of the <code>reservationToCompareAdmin</code> object are not reserved during the
	 *         <code>reservationToCompareAdmin</code> period. <code>false</code>, otherwise.
	 */
	public static boolean areResourcesAvailable(IReservationAdministration existingReservationAdmin,
			IReservationAdministration reservationToCompareAdmin) {

		// if existing reservation is in CREATED state, then it has no resources. If it's finished -> No need to compare.
		if (existingReservationAdmin.getState() == ReservationState.CREATED || existingReservationAdmin.getState() == ReservationState.FINISHED)
			return true;

		for (IRootResource reservationResource : reservationToCompareAdmin.getResources())
			if (existingReservationAdmin.getResources().contains(reservationResource))
				if (existingReservationAdmin.getPeriod().overlap(reservationToCompareAdmin.getPeriod()))
					return false;

		return true;

	}
}
