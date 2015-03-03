package org.mqnaas.network.api.reservation;

/*
 * #%L
 * MQNaaS :: Network API
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

	void planReservation(ReservationResource reservation) throws ResourceReservationException;

	void cancelPlannedReservation(ReservationResource reservation) throws ResourceReservationException;

}
