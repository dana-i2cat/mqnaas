package org.mqnaas.core.api.infrastructure;

import java.util.Collection;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * Capability providing mapping services between topology elements and
 * resources. To be bound to an infrastructure.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IInfrastructureAdministration extends ICapability {

	/**
	 * Maps a resource (device, link, port) to a {@link IResource}
	 */
	void defineDeviceMapping(IResource device, IResource resource);

	void defineLinkMapping(IResource link, IResource resource);

	/**
	 * Deletes an existing mapping for the given device
	 */
	void removeDeviceMapping(IResource resource);

	void removeLinkMapping(IResource resource);

	/**
	 * Returns the current mapping for the given resource (a device, link, or
	 * port)
	 */
	IResource getDeviceMapping(IResource resource);

	IResource getLinkMapping(IResource resource);

	/**
	 * Returns all device {@link IResource}s currently mapped.
	 */
	Collection<IResource> getMappedDevices();

	Collection<IResource> getMappedLinks();

}
