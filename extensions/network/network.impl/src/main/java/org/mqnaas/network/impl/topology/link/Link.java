package org.mqnaas.network.impl.topology.link;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.topology.link.ILinkAdministration;
import org.mqnaas.network.impl.topology.port.Port;

class Link {

	private IResource	link;
	IServiceProvider serviceProvider;

	public Link(IResource link, IServiceProvider serviceProvider) {
		this.link = link;
		this.serviceProvider = serviceProvider;
	}

	private ILinkAdministration getLinkAdministration()
			throws CapabilityNotFoundException {
		return serviceProvider.getCapability(link,
				ILinkAdministration.class);
	}

	public void setDestPort(Port port)
			throws CapabilityNotFoundException {
		getLinkAdministration().setSrcPort(port.getResource());
	}

	public void setSrcPort(Port port)
			throws CapabilityNotFoundException {
		getLinkAdministration().setDestPort(port.getResource());
	}

}