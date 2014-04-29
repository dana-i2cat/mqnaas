package org.mqnaas.core.api;

import com.google.common.collect.Multimap;

/**
 * <p>
 * <code>IBindingManagement</code> is one of the core capabilities of MQNaaS and is the capability providing the most basic services, the management
 * of available services based on the available resources (managed by {@link IRootResourceManagement} and the available capability implementations
 * (managed by the platform administrator by adding and removing bundles containing implementations).
 * </p>
 * 
 * <p>
 * It's main responsibilities are:
 * 
 * <ol>
 * <li><u>Manage the capabilities available in the platform.</u> This task has two aspects:
 * <ul>
 * <li>Manage the {@link ICapability}s available, e.g. which capability interfaces are defined</li>
 * <li>Manage the {@link ICapability} implementations available, e.g. which classes implement which {@link ICapability}s.
 * </ul>
 * </li>
 * <li><u>Manage the {@link IApplication}s available.</u> An <code>IApplication</code> is third party code requiring utilizing platform services to
 * provide its functionalities.</li>
 * <li><u>Listen to resource being added and removed (see {@link #resourceAdded(IResource)} and {@link #resourceRemoved(IResource)}) for details and
 * update the set of services available depending on available capability implementations and resources.</li>
 * </ol>
 * 
 * <p>
 * Some of these services are not available to the majority of platform users, but are reserved for the sole use of the core, e.g.
 * {@link #resourceAdded(IResource)} and {@link #resourceRemoved(IResource)}.
 * </p>
 */
public interface IBindingManagement extends ICapability {

	/**
	 * Returns all services of the given {@link IResource} as a {@link Multimap} organized as follows:
	 * 
	 * <ul>
	 * <li><u>Key</u>: The capability class, in which the services are defined</li>
	 * <li><u>Value
	 * </ul>
	 * : A list of {@link IService}s defined by the capability class</li> </ul>
	 * 
	 * The results always reflects the current state of available services in the platform, e.g. if dependencies for a specific service are not
	 * available, the service will not be present in the returned Multimap.
	 * 
	 * @param resource
	 *            The resource for which to return the services
	 * @return The available services organized in a {@link Multimap}
	 */
	Multimap<Class<? extends ICapability>, IService> getServices(
			IResource resource);

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
	IService getService(IResource resource, String serviceName);

	/**
	 * <p>
	 * Defines whether the services present in the given {@link ICapability} class should be bound to the given {@link IResource}. This is the service
	 * defining the automatic binding process of the platform.
	 * </p>
	 * 
	 * <p>
	 * Although this service is at the moment present in the {@link IBindingManagement} capability of the core, it is likely to be moved to a separate
	 * capability, e.g. <code>IBindingStrategy</code>, to be able to vary this aspect independently.
	 * </p>
	 * 
	 * @param resource
	 *            The resource for which the binding is checked
	 * @param capabilityClass
	 *            The class containing an implementation of one or more {@link ICapability}s.
	 * @return whether the {@link IResource} and the {@link Class} should be bound
	 */
	boolean shouldBeBound(IResource resource,
			Class<? extends ICapability> capabilityClass);

	/**
	 * <p>
	 * This is the service called by the {@link IRootResourceManagement} whenever a new {@link IResource} was added to the platform.
	 * </p>
	 * <p>
	 * The {@link IBindingManagement} implementation than
	 * <ol>
	 * <li>checks whether the added {@link IResource} can be bound to any of the currently available capability implementations (using
	 * {@link #shouldBeBound(IResource, Class)}),</li>
	 * <li>binds the {@link IResource} and the capability implementation,</li>
	 * <li>resolves all capability dependencies, and</li>
	 * <li>makes all services available which are defined in <b>all</b> newly resolved capability implementations.</li>
	 * </ol>
	 * 
	 * @param resource
	 *            The resource added to the platform
	 */
	void resourceAdded(IResource resource);

	/**
	 * <p>
	 * This is the service called by the {@link IRootResourceManagement} whenever a new {@link IResource} was removed from the platform.
	 * </p>
	 * <p>
	 * The {@link IBindingManagement} implementation than
	 * 
	 * <ol>
	 * <li>unbinds the {@link IResource} from all its capability implementations,</li>
	 * <li>unresolves all capability implementation dependencies, and</li>
	 * <li>removes all services which were provided by the given resource.</li>
	 * </ol>
	 * 
	 * @param resource
	 *            The resource removed from the platform
	 */
	void resourceRemoved(IResource resource);

	/**
	 * This is a service to play with during development and will not be part of the final API
	 */
	void printAvailableServices();

}
