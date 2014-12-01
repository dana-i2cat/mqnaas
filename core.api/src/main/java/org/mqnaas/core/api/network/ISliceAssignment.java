package org.mqnaas.core.api.network;

import java.util.Collection;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;

/**
 * Manages an assignment of slices to resources.
 * 
 * @author Georg Mansky-Kummert
 */
public interface ISliceAssignment extends ICapability {

	/**
	 * Defines a {@link Slice} for the given {@link IResource}. The resource has
	 * to be support slicing.
	 */
	void defineSlice(IResource resource, IResource slice);

	/**
	 * Remove a previously defined {@link Slice}.
	 */
	void removeSlice(IResource resource);

	/**
	 * Returns the {@link Slice} currently defined for the given
	 * {@link IRootResource}
	 */
	IResource getSlice(IResource resource);

	/**
	 * Returns all {@link IRootResource}s that currently have an associated
	 * {@link Slice}
	 */
	Collection<IResource> getSlices();

}
