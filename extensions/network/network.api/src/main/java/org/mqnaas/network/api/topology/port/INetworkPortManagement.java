package org.mqnaas.network.api.topology.port;

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * Manages a network's ports. To be bound to all networks. 
 * 
 * @author Georg Mansky-Kummert
 */
public interface INetworkPortManagement extends ICapability {

	void addPort(IResource port);
	
	void removePort(IResource port);
	
	List<IResource> getPorts();

}
