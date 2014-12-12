package org.mqnaas.core.api.slicing;

import java.util.Collection;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.RemovesResource;

/**
 * Implemented by and bound to resources supporting slicing, e.g. TSON, OpenFlow-Switches.
 * 
 * Used by a network when creating a virtual network.
 *
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface ISlicingCapability extends ICapability {

	@AddsResource
	IResource createSlice(IResource slice) throws SlicingException;

	@RemovesResource
	void removeSlice(IResource rootResource) throws SlicingException;

	public Collection<IResource> getSlices();

}
