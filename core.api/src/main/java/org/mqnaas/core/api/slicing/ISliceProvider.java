package org.mqnaas.core.api.slicing;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * Allows to access a resource's slice. To be bound to a sliceable resource.
 * 
 * @author Georg Mansky-Kummert
 */
public interface ISliceProvider extends ICapability {
 
	IResource getSlice();	
	
}
