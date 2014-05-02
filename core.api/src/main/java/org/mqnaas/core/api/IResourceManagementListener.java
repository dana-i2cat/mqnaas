package org.mqnaas.core.api;

/**
 * <code>IResourceManagementListener</code> is responsible of receiving notifications of {@link IResourceManagement} whenever a {@link IResource} is
 * added to or removed from the platform.
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
	 */
	void resourceAdded(IResource resource);

	/**
	 * <p>
	 * This is the service called by the {@link IResourceManagement} whenever a new {@link IResource} was removed from the platform.
	 * </p>
	 * <p>
	 * 
	 * @param resource
	 *            The resource removed from the platform
	 */
	void resourceRemoved(IResource resource);
}
