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
	 * Adds a slice unit, e.g. a new axis the slice space.
	 */
	void addUnit(Unit unit);

	/**
	 * Returns all units defined in this slice space in the order the were defined.
	 */
	Unit[] getUnits();

	/**
	 * Initializes the {@link Range} for the given {@link Unit}, e.g. the width of the available slicing elements along that axis.
	 */
	void setRange(Unit unit, Range range);

	/**
	 * Returns the {@link Range} defined for the given slice {@link Unit}. If the give <code>unit</code> is not defined in this slice,
	 * <code>null</code> is returned.
	 */
	Range getRange(Unit unit);

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
	 * Checks either the space of the given <code>slice</code> resource is available in the slice managed by this capability.
	 * 
	 * @param slice
	 *            Slice resource containing the space to be compared.
	 * @return <code>true</code> if the <code>slice</code> space is available in the slice managed by this capability. <code>false</code> otherwise.
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

	/**
	 * 
	 * @return
	 */
	boolean isInOperationalState();
	
	/**
	 * Service that returns the internal data representation of this slice.
	 * 
	 * FIXME This should not be public, but was the only way of implementing the logic in the presence of proxies.
	 * 
	 * @return the boolean array
	 */
	Object getData();

}
