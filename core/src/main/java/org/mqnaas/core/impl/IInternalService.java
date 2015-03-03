package org.mqnaas.core.impl;

/*
 * #%L
 * MQNaaS :: Core
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

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;

public interface IInternalService extends IService {

	/**
	 * <p>
	 * Sets the {@link IResource} coupled to this service.
	 * </p>
	 * Due to initialization issues, the internal service representation has to provide the possibility to set the coupled Resource.
	 * 
	 * @param resource
	 *            The resource to be set
	 */
	public void setResource(IResource resource);

	/**
	 * Execute the service with the given parameters
	 * 
	 * @param parameters
	 *            The parameters used when executing the service
	 * @return The service execution result
	 */
	public Object execute(Object[] parameters) throws InvocationTargetException;

}
