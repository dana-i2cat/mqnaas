package org.mqnaas.network.api.request;

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;

/**
 * Management capability for the network request.
 *  
 * @author Georg Mansky-Kummert
 */
public interface IRequestManagement extends ICapability {

	@AddsResource
	IResource createRequest();
	
	@RemovesResource
	void removeRequest(IResource request);
	
	@ListsResources
	List<IResource> getRequests();
	
}
