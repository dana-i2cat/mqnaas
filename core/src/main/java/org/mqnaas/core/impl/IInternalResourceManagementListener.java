package org.mqnaas.core.impl;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * <code>IInternalResourceManagementListener</code> is responsible of receiving notifications of {@link IResourceManagement} whenever a
 * {@link IResource} is added to or removed from the platform.
 * 
 */
public interface IInternalResourceManagementListener extends ICapability {
	/**
	 * <p>
	 * This is the service called by the {@link IResourceManagement} whenever a new {@link IResource} was added to the platform.
	 * </p>
	 * <p>
	 * 
	 * @param resource
	 *            The resource added to the platform
	 * @param addedTo
	 */
	void resourceCreated(IResource resource, CapabilityInstance addedTo);

	/**
	 * <p>
	 * This is the service called by the {@link IResourceManagement} whenever a new {@link IResource} was removed from the platform.
	 * </p>
	 * <p>
	 * 
	 * @param resource
	 *            The resource removed from the platform
	 * @param removedFrom
	 */
	void resourceDestroyed(IResource resource, CapabilityInstance removedFrom);
}
