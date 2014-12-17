package org.mqnaas.network.impl;

import java.util.List;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.Cube;
import org.mqnaas.core.api.slicing.ISliceAdministration;
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.api.slicing.Unit;
import org.mqnaas.core.impl.slicing.SliceResource;

/**
 * <p>
 * Wrapper class for {@link SliceResource}s to provide easier access to its capabilities.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 */
public class Slice {

	private IResource			slice;
	private IServiceProvider	serviceProvider;

	public Slice(IResource slice, IServiceProvider serviceProvider) {
		this.slice = slice;
		this.serviceProvider = serviceProvider;
	}

	public ISliceAdministration getSliceAdministration() {
		try {
			return serviceProvider.getCapability(slice, ISliceAdministration.class);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + slice, e);

		}
	}

	public IResource getSlice() {
		return slice;
	}

	public void setRange(Unit unit, Range range) {
		getSliceAdministration().setRange(unit, range);

	}

	public void addUnit(Unit unit) {
		getSliceAdministration().addUnit(unit);

	}

	public void setCubes(List<Cube> cubes) {
		getSliceAdministration().setCubes(cubes);

	}
}
