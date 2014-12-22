package org.mqnaas.network.impl.request;

import java.util.Collection;
import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.request.IRequestAdministration;
import org.mqnaas.network.api.request.IRequestResourceManagement;
import org.mqnaas.network.api.request.IRequestResourceMapping;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;

/**
 * Wraps access to the capabilities of the {@link RequestResource} to simplify access.
 * 
 * @author Georg Mansky-Kummert
 */
public class Request implements IRequestResourceMapping {

	private IServiceProvider	serviceProvider;
	private IResource			request;

	public Request(IResource request, IServiceProvider serviceProvider) {
		if (serviceProvider == null)
			throw new IllegalArgumentException("ServiceProvider must be given.");

		this.request = request;
		this.serviceProvider = serviceProvider;
	}

	public IResource getRequestResource() {
		return request;
	}

	@Override
	public IResource getMapping(IResource resource) {
		return getCapability(IRequestResourceMapping.class).getMapping(resource);
	}

	@Override
	public Collection<IResource> getMappedDevices() {
		return getCapability(IRequestResourceMapping.class).getMappedDevices();
	}
	
	@Override
	public void defineMapping(IResource requestResource, IResource rootResource) {
		getCapability(IRequestResourceMapping.class).defineMapping(requestResource, rootResource);
	}
	
	@Override
	public void removeMapping(IResource resource) {
		getCapability(IRequestResourceMapping.class).removeMapping(resource);
	}

	public List<IResource> getRootResources() {
		return getCapability(IRequestResourceManagement.class).getResources();
	}

	public IResource getMappedDevice(IResource resource) {
		return getMapping(resource);
	}

	public Period getPeriod() {
		return getCapability(IRequestAdministration.class).getPeriod();
	}

	public List<IResource> getLinks() throws CapabilityNotFoundException {
		return getCapability(ILinkManagement.class).getLinks();
	}

	private <C extends ICapability> C getCapability(Class<C> capabilityClass) {
		try {
			return serviceProvider.getCapability(request, capabilityClass);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + request, e);
		}
	}

	public List<IResource> getNetworkPorts() throws CapabilityNotFoundException {
		return getCapability(INetworkPortManagement.class).getPorts();

	}

	public IResource createResource(Type type) {
		return getCapability(IRequestResourceManagement.class).createResource(type);
	}

	public void addNetworkPort(IResource netPort) {
		getCapability(INetworkPortManagement.class).addPort(netPort);

	}

	public IResource createLink() {
		return getCapability(ILinkManagement.class).createLink();
	}

	public void setPeriod(Period period) {
		getCapability(IRequestAdministration.class).setPeriod(period);

	}

	public IResource getResource() {
		return request;
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

}