package org.mqnaas.network.api.topology.port;

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;

/**
 * Manages a {@link Device}'s ports. To be bound to a {@link Device}; 
 * 
 * @author Georg Mansky-Kummert
 */
public interface IPortManagement extends ICapability {

	@AddsResource
	IResource createPort();
	
	@RemovesResource
	void removePort(IResource port);
	
	@ListsResources
	List<IResource> getPorts();

}
