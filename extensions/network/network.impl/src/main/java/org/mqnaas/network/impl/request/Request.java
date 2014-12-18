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
public class Request {

	private IServiceProvider	serviceProvider;
	private IResource			request;

	public Request(IResource request, IServiceProvider serviceProvider) {
		this.request = request;
		this.serviceProvider = serviceProvider;
	}

	public IResource getRequestResource() {
		return request;
	}

	private IRequestResourceMapping getMapping() throws CapabilityNotFoundException {
		return getCapability(IRequestResourceMapping.class);
	}

	private IResource getMapping(IResource resource) throws CapabilityNotFoundException {
		return getMapping().getMapping(resource);
	}

	public Collection<IResource> getMappedDevices() throws CapabilityNotFoundException {
		return getMapping().getMappedDevices();
	}

	public List<IResource> getRootResources() throws CapabilityNotFoundException {
		return getCapability(IRequestResourceManagement.class).getResources();
	}

	public IResource getMappedDevice(IResource resource) throws CapabilityNotFoundException {
		return getMapping(resource);
	}

	public Period getPeriod() throws CapabilityNotFoundException {
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

	public void defineMapping(IResource virtResource, IResource phyResource) {
		getCapability(IRequestResourceMapping.class).defineMapping(virtResource, phyResource);

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

}