package org.mqnaas.network.impl;

import java.util.List;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.Cube;
import org.mqnaas.core.api.slicing.ISliceAdministration;
import org.mqnaas.core.api.slicing.IUnitManagement;
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

	public IUnitManagement getUnitAdministration() {
		try {
			return serviceProvider.getCapability(slice, IUnitManagement.class);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + slice, e);

		}
	}

	public IResource getSlice() {
		return slice;
	}

	public IResource addUnit(String name) {
		return getUnitAdministration().createUnit(name);
	}

	public void setCubes(List<Cube> cubes) {
		getSliceAdministration().setCubes(cubes);

	}

	public List<IResource> getUnits() {
		return getUnitAdministration().getUnits();
	}

	public String toMatrix() {

		StringBuilder sb = new StringBuilder();

		switch (getUnits().size()) {
			case 1:
				boolean d1[] = (boolean[]) getData();

				for (int x = 0; x < d1.length; x++) {
					if (d1[x])
						sb.append("X");
					else
						sb.append("O");
				}
				break;
			case 2:
				boolean d2[][] = (boolean[][]) getData();

				for (int x = 0; x < d2.length; x++) {
					for (int y = 0; y < d2[0].length; y++) {
						if (d2[x][y])
							sb.append("X");
						else
							sb.append("O");
					}
					sb.append("\n");
				}
				break;
			case 3:
				boolean d3[][][] = (boolean[][][]) getData();

				for (int x = 0; x < d3.length; x++) {
					for (int y = 0; y < d3[0].length; y++) {
						for (int z = 0; z < d3[0][0].length; z++) {
							if (d3[x][y][z])
								sb.append("X");
							else
								sb.append("O");
						}
						sb.append("\n");
					}
					sb.append("\n");
				}
				break;

		}

		return sb.toString();
	}

	private Object getData() {
		return getSliceAdministration().getData();

	}
}
