package org.mqnaas.network.impl;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;

class Network {

	private IResource			network;
	private IServiceProvider	serviceProvider;

	public Network(IResource network, IServiceProvider serviceProvider) {
		this.network = network;
		this.serviceProvider = serviceProvider;
	}

	public void addResource(IResource resource) throws CapabilityNotFoundException {
		getResourceManagementListener().resourceAdded(resource, getRootResourceAdministration(), IRootResourceAdministration.class);

	}

	private IResourceManagementListener getResourceManagementListener() throws CapabilityNotFoundException {
		return serviceProvider.getCapability(network, IResourceManagementListener.class);
	}

	private IRootResourceAdministration getRootResourceAdministration() throws CapabilityNotFoundException {
		return serviceProvider.getCapability(network, IRootResourceAdministration.class);
	}

}