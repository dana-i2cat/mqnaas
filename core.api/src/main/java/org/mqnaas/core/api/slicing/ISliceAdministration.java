package org.mqnaas.core.api.slicing;

import java.util.Collection;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * <p>
 * Management capability for the {@link Slice} resource
 * </p>
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface ISliceAdministration extends ICapability {

	/**
	 * Adds a new axis with the given <code>name<code> and <code>size</code> to the slice space.
	 * 
	 * @param name
	 *            Name of the slice unit to add.
	 * @param size
	 *            Length of the slice unit to be added.
	 * 
	 */
	void addUnit(String name, int size);

	/**
	 * Initializes the given {@link Cube} in the space of this slice, e.g. defines the elements within the <code>cube</code> as slicing units.
	 */
	void setCubes(Collection<Cube> cube);

	/**
	 * <p>
	 * Returns the smallest collection of {@link Cube}s representing the definition of this slice.
	 * </p>
	 * This representation may not coincide with the list of cubes used to initialize the space using {@link #set(Cube)}, although the contained slice
	 * elements will be the same.
	 */
	Collection<Cube> getCubes();

	/**
	 * Returns the smallest collection of {@link Cube} representing the available slicing units of this slice. This corresponds to the current state
	 * of the slice.
	 */
	Collection<Cube> getAvailableCubes();

	/**
	 * Cheks either the space of the given <code>slice</code> resource is available in the slice managed by this capability.
	 * 
	 * @param slice
	 *            Slice resource containing the space to be compared.
	 * @return <code>true</code> if the <code>slice</code> space is avaiable in the slice managed by this capability. <code>false</code> otherwise.
	 * @throws SlicingException
	 *             If the given <code>slice</code> structure does not match the one of the slice managed by this capability. For example, they have
	 *             different dimensions, the length of the units are not the same, etc.
	 */
	boolean contains(IResource slice) throws SlicingException;

	/**
	 * Marks as unavailable the space of the given <code>slice</code> in the slice managed by this capability. The parameter could be seen as a
	 * subspace of the current slice space.
	 * 
	 * @param slice
	 *            Slice containing the space to be marked as reserved.
	 * @throws SlicingException
	 *             If the structure of the slices differs (number of dimensions, length of units, etc) or if the given space is not available in the
	 *             current slice.
	 */
	void cut(IResource slice) throws SlicingException;

	/**
	 * Marks as available the space of the given <code>slice</code> in the slice managed by this capability. The intersection between the spaces of
	 * both capabilities must be <code>null</code>
	 * 
	 * @param slice
	 *            Slice containing the space to be added to the current capability..
	 * @throws SlicingException
	 *             If the structure of the slices differs (number of dimensions, length of units, etc) or if the given space is already part of the
	 *             current slice.
	 */
	void add(IResource slice) throws SlicingException;

}
