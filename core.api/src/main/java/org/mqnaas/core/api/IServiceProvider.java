package org.mqnaas.core.api;

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
	Multimap<Class<? extends ICapability>, IService> getServices(IResource resource);

	/**
	 * Returns the service with a specific name of the given {@link IResource}.
	 * 
	 * @param resource
	 *            The resource for which to return the service
	 * @param serviceName
	 *            The name of the service
	 * @return The service with the given name or <code>null</code>, if no such service exists or if the service exists but is not available at the
	 *         moment.
	 */
	IService getService(IResource resource, String serviceName, Class<?>... parameters) throws ServiceNotFoundException;

	/**
	 * FIXME This is a service to play with during development and will not be part of the final API
	 */
	void printAvailableServices();
}
