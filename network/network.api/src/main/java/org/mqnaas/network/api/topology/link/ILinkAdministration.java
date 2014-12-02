package org.mqnaas.network.api.topology.link;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * Manages a topology's links. To be bound to a link resource.
 * 
 * @author Georg Mansky-Kummert
 */
public interface ILinkAdministration extends ICapability {

	void setSrcPort(IResource srcPort);
	
	IResource getSrcPort();

	void setDestPort(IResource destPort);

	IResource getDestPort();

}
