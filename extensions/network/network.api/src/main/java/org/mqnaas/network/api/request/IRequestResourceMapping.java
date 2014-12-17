package org.mqnaas.network.api.request;

import java.util.Collection;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * Maps requested resources to {@link IResource}s of the corresponding network.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IRequestResourceMapping extends ICapability {

	void defineMapping(IResource requestResource, IResource rootResource);

	IResource getMapping(IResource requestResource);

	void removeMapping(IResource resource);

	Collection<IResource> getMappedDevices();

}
