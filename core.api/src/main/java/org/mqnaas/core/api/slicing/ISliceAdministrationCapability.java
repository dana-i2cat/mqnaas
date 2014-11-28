package org.mqnaas.core.api.slicing;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface ISliceAdministrationCapability extends ICapability {

	// used in the init to add dimensions to the slice
	void addUnit(String name, int size);

	// used in the init to define which cubes are available
	void set(SliceCube... cube);

	boolean contains(IResource slice) throws SlicingException;

	void cut(IResource slice) throws SlicingException;

	void add(IResource slice) throws SlicingException;

}
