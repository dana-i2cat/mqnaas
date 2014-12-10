package org.mqnaas.network.impl.request;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.network.api.request.IRequestResourceMapping;
import org.mqnaas.network.impl.RequestResource;

/**
 * Implementation of the {@link IRequestResourceMapping} capability using a {@link ConcurrentHashMap}. This implementation is bound to the
 * {@link RequestResource} to provide the mapping information necessary when creating the network.
 * 
 * This mapping is thought to be provided by different sources, e.g. the user itself, or an algorithm automatically mapping a request to an
 * infrastructure.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestResourceMapping implements IRequestResourceMapping {

	private Map<IResource, IRootResource>	mapping;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	@Override
	public void defineMapping(IResource requestResource, IRootResource rootResource) {
		mapping.put(requestResource, rootResource);
	}

	@Override
	public IRootResource getMapping(IResource requestResource) {
		return mapping.get(requestResource);
	}

	@Override
	public void removeMapping(IResource resource) {
		mapping.remove(resource);
	}

	@Override
	public Collection<IResource> getMappedDevices() {
		return mapping.keySet();
	}

	@Override
	public void activate() {
		mapping = new ConcurrentHashMap<IResource, IRootResource>();
	}

	@Override
	public void deactivate() {
	}

}
