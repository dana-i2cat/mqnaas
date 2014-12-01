package org.mqnaas.core.api.topology.link;

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;


/**
 * Manages a topology's links. To be bound to a topology resource.
 * 
 * @author Georg Mansky-Kummert
 */
public interface ILinkManagement extends ICapability {
	
	@AddsResource
	IResource createLink();
	
	@RemovesResource
	void removeLink(IResource link);
	
	@ListsResources
	List<IResource> getLinks();
	
}
