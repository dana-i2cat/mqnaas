package org.mqnaas.network.impl;

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
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.api.request.IRequestManagement;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;

public class Network {

	private IResource			network;
	private IServiceProvider	serviceProvider;

	public Network(IResource network, IServiceProvider serviceProvider) {
		this.network = network;
		this.serviceProvider = serviceProvider;
	}

	public List<IRootResource> getResources() {
		return getCapability(IRootResourceProvider.class).getRootResources();
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

	public IRootResource createResource(Specification spec, List<Endpoint> endpoints) throws InstantiationException, IllegalAccessException {
		return getRootResourceAdministration().createRootResource(RootResourceDescriptor.create(spec, endpoints));

	}

	private IRootResourceAdministration getRootResourceAdministration() {
		return getCapability(IRootResourceAdministration.class);
	}

	private IRootResourceProvider getRootResourceProvider() {
		return getCapability(IRootResourceProvider.class);
	}

	public List<IRootResource> getRootResources(Type type, String model, String version) throws ResourceNotFoundException {
		return getRootResourceProvider().getRootResources(type, model, version);
	}
}