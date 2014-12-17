package org.mqnaas.core.api.slicing;

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
public interface IUnitManagement extends ICapability {
	
	@AddsResource
	IResource createUnit(String name);
	
	@RemovesResource
	void removeUnit(IResource unit);
	
	@ListsResources
	List<IResource> getUnits();
	
}
