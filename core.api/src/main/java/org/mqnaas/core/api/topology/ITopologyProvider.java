package org.mqnaas.core.api.topology;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * Allows to access the network's {@link Topology}. To be bound to a network.
 * 
 * @author Georg Mansky-Kummert
 */
public interface ITopologyProvider extends ICapability {

	IResource getTopology();	
	
}
