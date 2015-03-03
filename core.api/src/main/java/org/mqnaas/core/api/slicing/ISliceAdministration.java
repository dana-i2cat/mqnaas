package org.mqnaas.core.api.slicing;

/*
 * #%L
 * MQNaaS :: Core.API
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
	 * Initializes the given {@link Cube}s in the space of this slice, e.g. defines the elements within the <code>cube</code>s as slicing units.
	 */
	void setCubes(CubesList cubes);

	/**
	 * Initializes the given {@link Cube}s in the space of this slice, e.g. clears the elements within the <code>cube</code>s from being slicing
	 * units.
	 * 
	 * @param cubes
	 */
	void unsetCubes(CubesList cubes);

	/**
	 * <p>
	 * Returns the smallest collection of {@link Cube}s representing the definition of this slice.
	 * </p>
	 * This representation may not coincide with the list of cubes used to initialize the space using {@link #set(Cube)}, although the contained slice
	 * elements will be the same.
	 */
	CubesList getCubes();

	/**
	 * Returns the smallest collection of {@link Cube} representing the available slicing units of this slice. This corresponds to the current state
	 * of the slice.
	 */
	CubesList getAvailableCubes();

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
