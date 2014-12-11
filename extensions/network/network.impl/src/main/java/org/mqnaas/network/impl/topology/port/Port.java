package org.mqnaas.network.impl.topology.port;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.topology.port.IPortAdministration;

public class Port {

	private IResource	port;
	private IServiceProvider serviceProvider;

	public Port(IResource port, IServiceProvider serviceProvider) {
		this.port = port;
		this.serviceProvider = serviceProvider;
	}

	private IPortAdministration getPortAdministration()
			throws CapabilityNotFoundException {
		return serviceProvider.getCapability(port,
				IPortAdministration.class);
	}

	public void setName(String name) throws CapabilityNotFoundException {
		getPortAdministration().setName(name);
	}

	public IResource getResource() {
		return port;
	}

}