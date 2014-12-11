package org.mqnaas.network.impl.request;

import java.util.Collection;
import java.util.List;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.request.IRequestResourceMapping;
import org.mqnaas.network.impl.RequestResource;

/**
 * Wraps access to the capabilities of the {@link RequestResource} to simplify access.
 * 
 * @author Georg Mansky-Kummert
 */
public class Request {

	private IServiceProvider	serviceProvider;
	private IResource			request;

	public Request(IResource request, IServiceProvider serviceProvider) {
		this.request = request;
		this.serviceProvider = serviceProvider;
	}

	private IRequestResourceMapping getMapping() throws CapabilityNotFoundException {
		return serviceProvider.getCapability(request, IRequestResourceMapping.class);
	}

	private IResource getMapping(IResource resource) throws CapabilityNotFoundException {
		return getMapping().getMapping(resource);
	}

	public Collection<IResource> getMappedDevices() throws CapabilityNotFoundException {
		return getMapping().getMappedDevices();
	}

	public List<IRootResource> getRootResources() throws CapabilityNotFoundException {
		return serviceProvider.getCapability(request, IRootResourceProvider.class).getRootResources();
	}

	public IResource getMappedDevice(IResource resource) throws CapabilityNotFoundException {
		return getMapping(resource);
	}

}