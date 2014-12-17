package org.mqnaas.network.impl;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.topology.link.ILinkAdministration;
import org.mqnaas.network.impl.topology.link.LinkResource;

/**
 * 
 * <p>
 * Wrapper class of {@link LinkResource}s in order to provide easier access to its capabilities and capabilities methods.
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class Link {

	private IResource			link;
	private IServiceProvider	serviceProvider;

	public Link(IResource link, IServiceProvider serviceProvider) {
		super();
		this.link = link;
		this.serviceProvider = serviceProvider;
	}

	public void setSrcPort(IResource port) {
		getLinkAdministration().setSrcPort(port);
	}

	public void setDstPort(IResource port) {
		getLinkAdministration().setDestPort(port);

	}

	public IResource getSrcPort() {
		return getLinkAdministration().getSrcPort();
	}

	public IResource getDstPort() {
		return getLinkAdministration().getDestPort();

	}

	private ILinkAdministration getLinkAdministration() {
		try {
			return serviceProvider.getCapability(link, ILinkAdministration.class);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + link, e);

		}
	}
}
