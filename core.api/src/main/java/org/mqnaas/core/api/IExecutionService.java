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

import java.lang.reflect.InvocationTargetException;

/**
 * <p>
 * <code>IExecutionService</code> is one of the core capabilities of MQNaaS.
 * </p>
 * 
 * <p>
 * It offers the ability of executing services. It executes services transactionally considering security and concurrency.
 */
public interface IExecutionService extends ICapability {

	/**
	 * <p>
	 * Executes the given {@link IService} synchronously with the given parameters and returns the result.
	 * </p>
	 * 
	 * <p>
	 * The execution involves the following steps:
	 * <ol>
	 * <li>check if the identity running the current thread has sufficient permissions to execute the service</li>
	 * <li>acquire a lock on the resource to which the service is bound,</li>
	 * <li>assure execution within a transaction:
	 * <ul>
	 * <li>if <u>no transaction</u> exists: open a new transaction</li>
	 * <li>if a <u>transaction already</u> exists: join this transaction</li>
	 * </ul>
	 * <li>execute the service</li>
	 * <li>notify all registered services observing the service executed</li>
	 * </ol>
	 * 
	 * @param service
	 *            The service to be executed
	 * @param parameters
	 *            The parameters with which to execute the given service
	 * 
	 * @return The result of the service execution
	 */
	Object execute(IService service, Object[] parameters) throws InvocationTargetException;

}
