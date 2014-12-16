package org.mqnaas.network.impl;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.topology.link.ILinkAdministration;

class LinkWrapper {

	private IResource			link;
	private IServiceProvider	serviceProvider;

	public LinkWrapper(IResource link, IServiceProvider serviceProvider) {
		super();
		this.link = link;
		this.serviceProvider = serviceProvider;
	}

	public void setSrcPort(IResource port) throws CapabilityNotFoundException {
		getLinkAdministration().setSrcPort(port);
	}

	public void setDstPort(IResource port) throws CapabilityNotFoundException {
		getLinkAdministration().setDestPort(port);

	}

	public IResource getSrcPort() throws CapabilityNotFoundException {
		return getLinkAdministration().getSrcPort();
	}

	public IResource getDstPort() throws CapabilityNotFoundException {
		return getLinkAdministration().getDestPort();

	}

	private ILinkAdministration getLinkAdministration() throws CapabilityNotFoundException {
		return serviceProvider.getCapability(link, ILinkAdministration.class);
	}
}
