package org.mqnaas.network.api;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.annotations.AddsResource;

public interface INetworkManagement extends IBaseNetworkManagement {

	
	/**
	 * Returns the new network resource created.
	 */
	@AddsResource
	IRootResource createNetwork(IResource request);

}
