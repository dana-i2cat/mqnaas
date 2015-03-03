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
 * <p>
 * An <code>IObservationFilter</code> is responsible for deciding whether a given {@link IService} should be monitored.
 * </p>
 * <p>
 * After all service executions in the core (see {@link IExecutionService} for details, registered <code>IObservationsFilter</code>s are utilized to
 * determine whether a service needs to be notified or not.
 * </p>
 * 
 * @see IExecutionService {@link IExecutionService#registerObservation(IObservationFilter, IService)} for details about its usage.
 * 
 */
public interface IObservationFilter {

	/**
	 * <p>
	 * Decide, whether the given {@link IService} is observed by this filter for the given execution parameters.
	 * <p>
	 * <p>
	 * If <code>true</code> is returned, the platform will launch the registered observation service. See
	 * {@link IExecutionService#registerObservation(IObservationFilter, IService)} for details.
	 * </p>
	 * 
	 * @param service
	 *            The service executed by the platform, which could be observed
	 * @param parameters
	 *            The parameters with which the service was executed
	 * 
	 * @return Whether this filter observes the given service
	 */
	boolean observes(IService service, Object[] parameters);

	/**
	 * <p>
	 * If {@link #observes(IService, Object[])} returns <code>true</code> this service is called to allow the implementor to modify the parameters,
	 * with which the observation service is called.
	 * </p>
	 * <b>TODO</b>: This should be moved as a parameter to {@link IExecutionService#registerObservation(IObservationFilter, IService)} .
	 * 
	 * @param service
	 *            The service, which was observed
	 * @param parameters
	 *            The parameters, with which the service was called
	 * @param result
	 *            The result of the observed service's execution
	 * 
	 * @return The reworked parameters for the observation service call
	 */
	Object[] getParameters(IService service, Object[] parameters, Object result);
}