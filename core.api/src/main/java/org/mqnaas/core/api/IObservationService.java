package org.mqnaas.core.api;

/*
 * #%L
 * MQNaaS :: Core.API
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 * 
 * <p>
 * <code>IObservationService</code> is one of the core capabilities of MQNaaS.
 * </p>
 * 
 * After successful service execution (see {@link IExecutionService}), service observers are notified. It allow the registration of services to
 * observe the service execution.
 */
public interface IObservationService extends ICapability {

	/**
	 * <p>
	 * Adds an observation: Whenever the given {@link IObservationFilter} applies, the given {@link IService} is executed.
	 * </p>
	 * <p>
	 * Whenever a service is executed (see {@link IExecutionService#execute(IService, Object[])} for a detailed explanation), the final step is to
	 * notify services observing other services. Which other services are observed by a service is defined by means of the {@link IObservationFilter}
	 * registered together with the observing service.
	 * 
	 * @param filter
	 *            The {@link IObservationFilter}, which determines which services are observed
	 * @param service
	 *            The {@link IService} to be executed if the filter applies
	 */
	void registerObservation(IObservationFilter filter, IService service);

}
