package org.mqnaas.network.api.request;

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;

/**
 * A simplified resource management capability used to manage network requests.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IRequestResourceManagement extends ICapability {

	@AddsResource
	IResource createResource(Type type);

	@RemovesResource
	void removeResource(IResource resource);

	@ListsResources
	List<IResource> getResources();

}
