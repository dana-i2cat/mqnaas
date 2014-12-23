package org.mqnaas.network.api;

import java.util.Collection;

import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;
import org.mqnaas.network.api.exceptions.NetworkReleaseException;

public interface IBaseNetworkManagement {

	/**
	 * Releases a previously created network
	 * 
	 * @throws NetworkReleaseException
	 */
	@RemovesResource
	void releaseNetwork(IRootResource resource) throws NetworkReleaseException;

	/**
	 * Returns all networks managed by this capability
	 */
	@ListsResources
	Collection<IRootResource> getNetworks();

}
