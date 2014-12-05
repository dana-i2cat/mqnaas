package org.mqnaas.core.api;

/**
 * <code>IResourceManagementListener</code> is responsible of receiving notifications of {@link IResourceManagement} whenever a {@link IResource} is
 * added to or removed from the platform.
 * 
 * One may observe services in this ICapability to react to Resource creation and removal in the platform. See {@link IObservationService}.
 * 
 */
public interface IResourceManagementListener extends ICapability {
	/**
	 * <p>
	 * This is the service called by the {@link IResourceManagement} whenever a new {@link IResource} was added to the platform.
	 * </p>
	 * <p>
	 * 
	 * @param resource
	 *            The resource added to the platform
	 * @param managedBy
	 *            The IApplication managing given resource
	 * @param parentInterface
	 * 			  The interface managing given resource in managedBy instance
	 */
	void resourceAdded(IResource resource, IApplication managedBy, Class<? extends IApplication> parentInterface);

	/**
	 * <p>
	 * This is the service called by the {@link IResourceManagement} whenever a new {@link IResource} was removed from the platform.
	 * </p>
	 * <p>
	 * 
	 * @param resource
	 *            The resource removed from the platform
	 * @param managedBy
	 *            The IApplication managing given resource
	 * @param parentInterface
	 * 			  The interface managing given resource in managedBy instance
	 */
	void resourceRemoved(IResource resource, IApplication managedBy, Class<? extends IApplication> parentInterface);
}
