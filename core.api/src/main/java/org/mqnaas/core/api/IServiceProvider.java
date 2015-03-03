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

import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;

import com.google.common.collect.Multimap;

/**
 * <code>IServiceProvider</code> is responsible of managing {@link IServices}'s in the system.
 * 
 */
public interface IServiceProvider extends ICapability {

	/**
	 * Returns all services of the given {@link IResource} as a {@link Multimap} organized as follows:
	 * 
	 * <ul>
	 * <li><u>Key</u>: The capability class, in which the services are defined</li>
	 * <li><u>Value</u>: A list of {@link IService}s defined by the capability class</li>
	 * </ul>
	 * 
	 * The results always reflects the current state of available services in the platform, e.g. if dependencies for a specific service are not
	 * available, the service will not be present in the returned Multimap.
	 * 
	 * @param resource
	 *            The resource for which to return the services
	 * @return The available services organized in a {@link Multimap}
	 */
	Multimap<Class<? extends IApplication>, IService> getServices(IResource resource);

	/**
	 * Returns the service with a specific name of the given {@link IResource}.
	 * 
	 * @param resource
	 *            The resource for which to return the service
	 * @param serviceName
	 *            The name of the service
	 * @param parameters
	 * @return The service with the given name or <code>null</code>, if no such service exists or if the service exists but is not available at the
	 *         moment.
	 */
	IService getService(IResource resource, String serviceName, Class<?>... parameters) throws ServiceNotFoundException;

	/**
	 * Returns the service with a specific name of the given {@link IApplication}.
	 * 
	 * @param application
	 * @param serviceName The name of the service
	 * @param parameters service parameter types
	 * @return The service with the given name.
	 * @throws ServiceNotFoundException if there is no such service available at the moment.
	 */
	IService getApplicationService(IApplication application, String serviceName, Class<?>... parameters) throws ServiceNotFoundException;



	/**
	 * Returns the capability instance of type <code>capabilityClass</code> bound to the given {@link IResource}
	 * 
	 * @param resource
	 *            Resource containing the capability to retrieve.
	 * @param capabilityClass
	 *            Class defining the capability type.
	 * @return Capability of type <code>capabilityClass</code> bound to the <code>resource</code> resource.
	 * 
	 * @throws CapabilityNotFoundException
	 *             If the given resource does not contain any capability of type <code>capabilityclass</code>
	 */
	<C extends ICapability> C getCapability(IResource resource, Class<C> capabilityClass) throws CapabilityNotFoundException;

	/**
	 * FIXME This is a service to play with during development and will not be part of the final API
	 */
	void printAvailableServices();

	<C extends ICapability> C getCapabilityInstance(IResource resource, Class<C> capabilityClass) throws CapabilityNotFoundException;
}
