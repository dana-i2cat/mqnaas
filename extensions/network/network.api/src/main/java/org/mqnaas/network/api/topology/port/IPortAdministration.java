package org.mqnaas.network.api.topology.port;

import org.mqnaas.core.api.ICapability;

/**
 * Allows the administration of a port. To be bound to the port.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IPortAdministration extends ICapability {

	void setName(String name);
	String getName();
	
}
