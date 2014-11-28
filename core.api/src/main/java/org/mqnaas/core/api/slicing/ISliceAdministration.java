package org.mqnaas.core.api.slicing;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface ISliceAdministration extends ICapability {

	// used in the init to add dimensions to the slice
	void addUnit(String name, int size);

	// used in the init to define which cubes are available
	void set(SliceCube cube);

	boolean contains(IResource slice);

	void cut(IResource slice);

	void add(IResource slice);

}
