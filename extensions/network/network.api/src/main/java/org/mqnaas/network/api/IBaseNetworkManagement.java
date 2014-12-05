package org.mqnaas.network.api;

import java.util.Collection;

import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;

public interface IBaseNetworkManagement {

	/**
	 * Releases a previously created network
	 */
	@RemovesResource
	void releaseNetwork(IRootResource resource);

	/**
	 * Returns all networks managed by this capability
	 */
	@ListsResources
	Collection<IRootResource> getNetworks();
	
}
