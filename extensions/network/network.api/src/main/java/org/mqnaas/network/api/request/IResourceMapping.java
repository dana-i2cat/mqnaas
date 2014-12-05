package org.mqnaas.network.api.request;

import java.util.Collection;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;

/**
 * Maps requested resources to {@link IRootResource}s of the corresponding
 * network.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IResourceMapping extends ICapability {

	void defineMapping(IResource requestResource, IRootResource rootResource);
	
	IRootResource getMapping(IResource requestResource);
	
	void removeMapping(IResource resource);
	
	Collection<IResource> getMappedDevices();
	
}
