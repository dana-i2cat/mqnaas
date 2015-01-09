package org.mqnaas.network.impl;

import java.util.Collection;
import java.util.List;

import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.network.api.exceptions.NetworkCreationException;
import org.mqnaas.network.api.exceptions.NetworkReleaseException;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.api.request.IRequestManagement;
import org.mqnaas.network.api.request.IRequestResourceManagement;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;

public class Network implements IRootResourceProvider {

	private IResource			network;
	private IServiceProvider	serviceProvider;

	public Network(IResource network, IServiceProvider serviceProvider) {
		this.network = network;
		this.serviceProvider = serviceProvider;
	}

	public <C extends ICapability> C getCapability(Class<C> capabilityClass) {
		try {
			return serviceProvider.getCapability(network, capabilityClass);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + network, e);
		}
	}

	public List<IResource> getLinks() {
		return getCapability(ILinkManagement.class).getLinks();
	}

	public List<IResource> getPorts() {
		return getCapability(INetworkPortManagement.class).getPorts();
	}

	public IResource createRequest() {
		return getCapability(IRequestManagement.class).createRequest();

	}

	public IResource getNetworkResource() {
		return network;
	}

	public IRootResource createVirtualNetwork(IResource request) throws NetworkCreationException {
		return getCapability(IRequestBasedNetworkManagement.class).createNetwork(request);
	}

	public void releaseVirtualNetwork(IRootResource virtualNetwork) throws NetworkReleaseException {
		getCapability(IRequestBasedNetworkManagement.class).releaseNetwork(virtualNetwork);

	}

	public IRootResource createResource(Specification spec, List<Endpoint> endpoints) throws InstantiationException, IllegalAccessException {
		return getRootResourceAdministration().createRootResource(RootResourceDescriptor.create(spec, endpoints));

	}

	public IResource createRequestResource(Type type) {
		return getCapability(IRequestResourceManagement.class).createResource(type);
	}

	private IRootResourceAdministration getRootResourceAdministration() {
		return getCapability(IRootResourceAdministration.class);
	}

	@Override
	public List<IRootResource> getRootResources(Type type, String model, String version) throws ResourceNotFoundException {
		return getCapability(IRootResourceProvider.class).getRootResources(type, model, version);
	}

	@Override
	public List<IRootResource> getRootResources() {
		return getCapability(IRootResourceProvider.class).getRootResources();
	}

	@Override
	public IRootResource getRootResource(String id) throws ResourceNotFoundException {
		return getCapability(IRootResourceProvider.class).getRootResource(id);
	}

	@Override
	public void setRootResources(Collection<IRootResource> rootResources) {
		getCapability(IRootResourceProvider.class).setRootResources(rootResources);
	}

	@Override
	public void activate() {
	}

	public List<IResource> getNetworkSubResources() {
		return getRequestResourceManagement().getResources();
	}

	private IRequestResourceManagement getRequestResourceManagement() {
		return getCapability(IRequestResourceManagement.class);
	}

	public void removeResource(IRootResource resource) throws ResourceNotFoundException {
		getRootResourceAdministration().removeRootResource(resource);
	}

	@Override
	public void deactivate() {
	}

	public IResource createLink() {
		return getCapability(ILinkManagement.class).createLink();

	}

	public void addPort(IResource port) {
		getCapability(INetworkPortManagement.class).addPort(port);

	}

}