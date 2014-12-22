package org.mqnaas.core.impl.slicing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.Cube;
import org.mqnaas.core.api.slicing.CubesList;
import org.mqnaas.core.api.slicing.ISliceAdministration;
import org.mqnaas.core.api.slicing.IUnitManagement;
import org.mqnaas.core.api.slicing.SlicingException;

/**
 * Wrapper to simplify access to the slice's capabilities.
 * 
 * @author Georg Mansky-Kummert
 */
class Slice {

	private SliceResource		slice;
	private IServiceProvider	serviceProvider;

	public Slice(IResource slice, IServiceProvider serviceProvider) {
		if (slice == null)
			throw new IllegalArgumentException("Resource must be given.");
		if (!(slice instanceof SliceResource))
			throw new IllegalArgumentException("Resource must be of type " + SliceResource.class.getName() + ", but is of type + " + slice.getClass()
					.getName());

		this.slice = (SliceResource) slice;
		this.serviceProvider = serviceProvider;
	}

	public IResource getResource() {
		return slice;
	}

	private ISliceAdministration getAdministration() {
		try {
			return serviceProvider.getCapability(slice, ISliceAdministration.class);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + slice, e);
		}
	}

	private IUnitManagement getUnitManagement() {
		try {
			return serviceProvider.getCapability(slice, IUnitManagement.class);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + slice, e);
		}
	}

	public List<Unit> getUnits() {

		List<Unit> units = new ArrayList<Unit>();

		for (IResource unitResource : getUnitManagement().getUnits()) {
			units.add(new Unit(unitResource, serviceProvider));
		}

		return units;
	}

	public Object getData() {
		return getAdministration().getData();
	}

	public boolean isInOperationalState() {
		return getAdministration().isInOperationalState();
	}

	public Unit addUnit(String name) {
		return new Unit(getUnitManagement().createUnit(name), serviceProvider);
	}

	public void setCubes(List<Cube> cubes) {
		getAdministration().setCubes(new CubesList(cubes));
	}

	public boolean get(int[] cords) {
		return ((SliceAdministration) getAdministration()).get(cords);
	}

	public boolean contains(Slice otherSlice) throws SlicingException {
		return getAdministration().contains(otherSlice.slice);
	}

	public void add(Slice otherSlice) throws SlicingException {
		getAdministration().add(otherSlice.slice);
	}

	public void unset(Collection<Cube> cubes) {
		getAdministration().unsetCubes(new CubesList(cubes));
	}

	public void unset(Cube cube) {
		unset(Arrays.asList(new Cube[] { cube }));
	}

	public void cut(Slice otherSlice) throws SlicingException {
		getAdministration().cut(otherSlice.slice);
	}

	public Collection<Cube> getCubes() {
		return getAdministration().getCubes();
	}

	public Collection<Cube> getAvailableCubes() {
		return getAdministration().getAvailableCubes();
	}

	public void initData() {
		SliceAdministration administration = (SliceAdministration) getAdministration();

		administration.originalData = null;
		administration.currentData = null;
	}

	@Override
	public String toString() {
		return "Slice [id=" + slice.getId() + ", units=" + getUnits() + "]\nCubes defined: " + getCubes() + "\nCubes available: " + getAvailableCubes();
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

}
