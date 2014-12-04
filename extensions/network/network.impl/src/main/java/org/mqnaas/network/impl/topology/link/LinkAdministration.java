package org.mqnaas.network.impl.topology.link;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.network.api.topology.link.ILinkAdministration;

/**
 * Implementation of the {@link ILinkAdministration} capability which is bound
 * to a {@link LinkResource}.
 * 
 * @author Georg Mansky-Kummert
 */
public class LinkAdministration implements ILinkAdministration {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof LinkResource;
	}

	private IResource srcPort, destPort;

	@DependingOn
	private IResourceManagementListener resourceManagementListener;

	@Override
	public void activate() {
		// TODO: persistence
	}

	@Override
	public void deactivate() {
		// TODO: persistence
	}

	@Override
	public IResource getSrcPort() {
		return srcPort;
	}

	@Override
	public void setSrcPort(IResource srcPort) {
		this.srcPort = srcPort;
	}

	@Override
	public IResource getDestPort() {
		return destPort;
	}

	@Override
	public void setDestPort(IResource destPort) {
		this.destPort = destPort;
	}

}
