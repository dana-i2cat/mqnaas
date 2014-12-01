package org.mqnaas.core.impl.slicing;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.SerializationUtils;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceAdministrationCapability;
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.api.slicing.SliceCube;
import org.mqnaas.core.api.slicing.SlicingException;
import org.mqnaas.core.api.slicing.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class SliceAdministrationCapability implements ISliceAdministrationCapability {

	private static final Logger	log	= LoggerFactory.getLogger(SliceAdministrationCapability.class);

	private List<Unit>			units;
	private List<Integer>		sizes;
	Object						originalData;
	Object						currentData;

	@DependingOn
	IServiceProvider			serviceProvider;

	public SliceAdministrationCapability() {
	}

	@Override
	public void activate() {
		units = new CopyOnWriteArrayList<Unit>();
		sizes = new CopyOnWriteArrayList<Integer>();
	}

	@Override
	public void deactivate() {
		// TODO should the original and current data be set to "null"
	}

	@Override
	public void addUnit(String name, int size) {
		units.add(new Unit(name));
		sizes.add(size);
	}

	/**
	 * Initializes the given {@link SliceCube} in the space of this slice, e.g. defines the elements within the <code>cube</code> as slicing units.
	 *
	 * IMPORTANT: The original slice information will contain the values of the set of {@link SliceCube} passed as arguments the first time this
	 * method is called!
	 */
	@Override
	public void setCubes(Collection<SliceCube> cubes) {
		initData();

		int[] lowerBounds = new int[units.size()];
		int[] upperBounds = new int[units.size()];

		for (SliceCube cube : cubes) {

			Range[] ranges = cube.getRanges();
			for (int i = 0; i < units.size(); i++) {
				lowerBounds[i] = ranges[i].getLowerBound();
				upperBounds[i] = ranges[i].getUpperBound();
			}

			SetOperation set = new SetOperation();
			executeOperation(null, lowerBounds, upperBounds, set);
		}

		// if is the first time this method is call, we must initialize the originalData values as a copy of the currentData one.
		if (originalData == null)
			originalData = cloneSliceData(currentData, units.size());
	}

	@Override
	public boolean contains(IResource slice) throws SlicingException {
		initData();

		compareSliceDefinition(getSliceAdministration(slice));

		int[] lbs = new int[units.size()], ubs = new int[units.size()];

		initUpperBounds(ubs);

		ContainsOperation contains = new ContainsOperation();
		executeOperation(getSliceAdministration(slice), lbs, ubs, contains);
		return contains.getResult();
	}

	@Override
	public void cut(IResource slice) throws SlicingException {

		log.info("Cutting slice");

		initData();

		SliceAdministrationCapability otherSliceAdminCapab = getSliceAdministration(slice);

		compareSliceDefinition(otherSliceAdminCapab);

		int[] lbs = new int[units.size()], ubs = new int[units.size()];

		initUpperBounds(ubs);

		ContainsOperation contains = new ContainsOperation();
		executeOperation(otherSliceAdminCapab, lbs, ubs, contains);

		if (!contains.getResult())
			throw new SlicingException("Given slice contains values that are not in the original slice.");

		CutOperation cut = new CutOperation();
		executeOperation(otherSliceAdminCapab, lbs, ubs, cut);

		log.info("Slice cut");

	}

	@Override
	public void add(IResource slice) throws SlicingException {

		log.info("Adding slice.");

		initData();

		SliceAdministrationCapability otherSliceAdminCapab = getSliceAdministration(slice);

		compareSliceDefinition(otherSliceAdminCapab);

		if (otherSliceAdminCapab.isInOperationalState())
			throw new SlicingException("Can not add slice to current one since given slice is in operational state.");

		int[] lbs = new int[units.size()], ubs = new int[units.size()];

		initUpperBounds(ubs);

		NotContainsOperation preAdd = new NotContainsOperation();
		executeOperation(otherSliceAdminCapab, lbs, ubs, preAdd);

		if (!preAdd.getResult())
			throw new SlicingException("Given slice contains values that are already in the original slice.");

		AddOperation add = new AddOperation();
		executeOperation(otherSliceAdminCapab, lbs, ubs, add);

		log.info("Slice added");

	}

	@Override
	public Collection<SliceCube> getCubes() {
		return compatize(originalData);
	}

	@Override
	public Collection<SliceCube> getAvailableCubes() {
		return compatize(currentData);

	}

	/**
	 * Gets the current value of the slice unit specified by the indexes stored in the <code>coords</code> array.
	 * 
	 * @param coords
	 *            Position of the slice which value we want to retrieve.
	 * @return The current state of the slice unit. <code>true</code> if the slice unit is part of the slice and it's available. <code>false</code>
	 *         otherwise.
	 * 
	 */
	public boolean get(int[] coords) {
		return get(currentData, coords);
	}

	/**
	 * A {@link Slice} is in operational state if it's current space and the original one does not match. That means, if the slice has been divided in
	 * sub-slices.
	 * 
	 * @return <code>true</code> If any of the initial cube of the slice space has been assigned to another sub-slice. <code>false</code> otherwise.
	 */
	boolean isInOperationalState() {

		int[] lbs = new int[units.size()], ubs = new int[units.size()];
		initUpperBounds(ubs);

		int sizes[] = new int[ubs.length];
		for (int i = 0; i < ubs.length; i++)
			sizes[i] = ubs[i] + 1;

		SliceAdministrationCapability other = new SliceAdministrationCapability();
		other.currentData = this.originalData;
		other.sizes = this.sizes;
		other.units = this.units;

		ContainsOperation contains = new ContainsOperation();
		executeOperation(other, lbs, ubs, contains);

		return !contains.getResult();
	}

	/**
	 * Marks the set of cubes as unavailable in the slice space. Operation will be performed in the current space structure.
	 * 
	 * @param cubes
	 *            Cubes that will be markes as unavaiable in the current live space.
	 */
	void unset(SliceCube... cubes) {
		int[] lowerBounds = new int[units.size()];
		int[] upperBounds = new int[units.size()];

		for (SliceCube cube : cubes) {
			Range[] ranges = cube.getRanges();
			for (int i = 0; i < units.size(); i++) {
				lowerBounds[i] = ranges[i].getLowerBound();
				upperBounds[i] = ranges[i].getUpperBound();
			}

			UnsetOperation set = new UnsetOperation();
			executeOperation(null, lowerBounds, upperBounds, set);
		}
	}

	/**
	 * Initialize the internal structures of the this capability, i.e., the information of the current slice information.
	 */
	private void initData() {
		if (currentData == null) {
			int[] dimensions = new int[sizes.size()];
			int i = 0;
			for (int size : sizes)
				dimensions[i++] = size;

			currentData = Array.newInstance(boolean.class, dimensions);

		}
	}

	/**
	 * Clones the <code>source</code> slice space. Up to 3D implemented.
	 */
	private Object cloneSliceData(Object source, int dimensions) {
		switch (dimensions) {
			case 1:
				return SerializationUtils.clone((boolean[]) source);
			case 2:
				return SerializationUtils.clone((boolean[][]) source);
			case 3:
				return SerializationUtils.clone((boolean[][][]) source);
			default:
				throw new RuntimeException(
						"Only up to three dimensions implemented");
		}
	}

	/**
	 * Sets the <code>value</code> boolean value into the <code>currentData</code> array position defined by the <code>coords</code> array. Up to
	 * three dimensions implemented.
	 * 
	 * @param coords
	 *            Position of the <code>data</code> array defined by an array of indexes.
	 * @param value
	 *            boolean value to be set in this position.
	 */
	private void set(int[] coords, boolean value) {
		switch (units.size()) {
			case 1:
				boolean d1[] = (boolean[]) currentData;
				d1[coords[0]] = value;
				break;
			case 2:
				boolean d2[][] = (boolean[][]) currentData;
				d2[coords[0]][coords[1]] = value;
				break;
			case 3:
				boolean d3[][][] = (boolean[][][]) currentData;
				d3[coords[0]][coords[1]][coords[2]] = value;
				break;
			default:
				throw new RuntimeException(
						"Only up to three dimensions implemented");
		}
	}

	/**
	 * Gets the value of the slice unit specified by the indexes stored in the <code>coords</code> array in the <code>data</code> slice information,
	 * which could be either the original slice information or the current one.
	 * 
	 * @param coords
	 *            Position of the slice which value we want to retrieve.
	 * @return The state of the slice unit in the <code>data</code> slice unit information. <code>true</code> if the slice unit is part of the slice
	 *         and it's available. <code>false</code> otherwise.
	 * 
	 */
	private boolean get(Object data, int[] coords) {
		switch (units.size()) {
			case 1:
				boolean d1[] = (boolean[]) data;
				return d1[coords[0]];
			case 2:
				boolean d2[][] = (boolean[][]) data;
				return d2[coords[0]][coords[1]];
			case 3:
				boolean d3[][][] = (boolean[][][]) data;
				return d3[coords[0]][coords[1]][coords[2]];
			default:
				throw new RuntimeException(
						"Only up to three dimensions implemented");
		}
	}

	/**
	 * Specifies and sets the upper bounds of each dimension of the slice information. It's generic for all number of dimendsions.
	 * 
	 * @param ubs
	 *            Array where the upper bounds will be stored.
	 */
	private void initUpperBounds(int[] ubs) {
		Object it = currentData;

		for (int i = 0; i < units.size(); i++) {
			ubs[i] = Array.getLength(it) - 1;
			it = Array.get(it, 0);
		}
	}

	/**
	 * Creates a copy of the <code>original</code> {@link SliceAdministrationCapability}
	 * 
	 * @param data
	 * @param sizes
	 * 
	 * @param original
	 *            SliceAdministrationCapability to be cloned.
	 */
	private SliceAdministrationCapability(List<Unit> units, List<Integer> sizes, Object sliceData) {
		this.units = new CopyOnWriteArrayList<Unit>(units);
		this.sizes = new CopyOnWriteArrayList<Integer>(sizes);
		currentData = cloneSliceData(sliceData, units.size());
		originalData = cloneSliceData(sliceData, units.size());

	}

	private List<SliceCube> compatize(Object data) {
		SliceAdministrationCapability visited = new SliceAdministrationCapability(this.units, this.sizes, data);

		int[] lbs = new int[units.size()], ubs = new int[units.size()];

		initUpperBounds(ubs);

		CubisizeOperation operation = new CubisizeOperation();
		executeOperation(visited, lbs, ubs, operation);

		return operation.getCubes();
	}

	private interface Operation {

		boolean execute(SliceAdministrationCapability other, int[] coords);
	}

	/**
	 * Specifies whether a specific position of the slice managed by this capability instance was part of the original slice information and is still
	 * available, if the slice managed by the other capability requires this position. The position is indicated by an array of integers, which length
	 * is the number of dimensions of the slice and the coords[i] contains the index of the i-axis.
	 */
	private class ContainsOperation implements Operation {

		private boolean	result	= true;

		@Override
		public boolean execute(SliceAdministrationCapability other, int[] coords) {
			if (other.get(coords)) {
				// the other slice needs this element
				result &= (get(currentData, coords) && get(originalData, coords));
				return result;
			}

			// the other slice does not need this element, its value is not important
			return true;
		}

		public boolean getResult() {
			return result;
		}

	}

	/**
	 * Specifies whether a specific position of the slice managed by this capability instance is not available in the current slice information, but
	 * was part of the original slice information, only if the slice managed by the other capability contanis this position. The position is indicated
	 * by an array of integers, which length is the number of dimensions of the slice and the coords[i] contains the index of the i-axis.
	 */
	private class NotContainsOperation implements Operation {

		private boolean	result	= true;

		@Override
		public boolean execute(SliceAdministrationCapability other, int[] coords) {
			if (other.get(coords) && get(coords))
				result = false;

			return result;

		}

		public boolean getResult() {
			return result;
		}

	}

	/**
	 * Sets as available a specific position of the current slice information managed by this capability. The position is indicated by an array of
	 * integers, which length is the number of dimensions of the slice and the coords[i] contains the index of the i-axis.
	 */
	private class SetOperation implements Operation {

		@Override
		public boolean execute(SliceAdministrationCapability other, int[] coords) {
			set(coords, true);

			return true;
		}

	}

	/**
	 * Sets as unavailable a specific position of the current slice information managed by this capability. The position is indicated by an array of
	 * integers, which length is the number of dimensions of the slice and the coords[i] contains the index of the i-axis.
	 */
	private class UnsetOperation implements Operation {

		@Override
		public boolean execute(SliceAdministrationCapability other, int[] coords) {
			set(coords, false);

			return true;
		}

	}

	/**
	 * If the slice information position of the slice managed by the other sliceAdminsitration capability was part of it, and it's not available in
	 * the current slice but was part of the slice managed by this capability instance, it sets the specific slice unit as avaiable in the current
	 * slice. The position is indicated by an array of integers, which length is the number of dimensions of the slice and the coords[i] contains the
	 * index of the i-axis.
	 */
	private class AddOperation implements Operation {

		@Override
		public boolean execute(SliceAdministrationCapability other, int[] coords) {
			// we can only add elements that were part of the original slice.
			if (other.get(coords) && get(originalData, coords))
				set(coords, true);

			return true;

		}

	}

	/**
	 * If the slice managed by the other sliceAdministration capability requires a specific position of the current slice manages by this capability
	 * instance, it sets this position as unavailable in the mentioned position of the current slice information. The position is indicated by an
	 * array of integers, which length is the number of dimensions of the slice and the coords[i] contains the index of the i-axis.
	 *
	 */
	private class CutOperation implements Operation {

		@Override
		public boolean execute(SliceAdministrationCapability other, int[] coords) {
			if (other.get(coords))
				set(coords, false);
			return true;
		}

	}

	/**
	 * Builds a list of {@link SliceCube} from the slice space.
	 */
	private class CubisizeOperation implements Operation {

		private List<SliceCube>	cubes	= new ArrayList<SliceCube>();

		@Override
		public boolean execute(SliceAdministrationCapability other, int[] coords) {
			if (get(coords)) {

				// Start the fill along all axis from that point
				int n = coords.length;
				int[] dimensions = new int[n];
				initUpperBounds(dimensions);

				// Initialize the cube: start with a single element
				Range[] ranges = new Range[n];
				for (int i = 0; i < n; i++) {
					ranges[i] = new Range(coords[i], coords[i]);
				}

				IsPartOfSliceOperation partOfSlice = new IsPartOfSliceOperation();

				int[] lbs = new int[n], ubs = new int[n];

				// Now search as far as possible along each axis
				for (int searchAxis = 0; searchAxis < n; searchAxis++) {

					// Is a search possible?
					if (ranges[searchAxis].getUpperBound() >= dimensions[searchAxis]) {
						continue;
					}

					partOfSlice.setResult(true);

					do {
						// Initialize the search cube
						for (int i = 0; i < n; i++) {
							lbs[i] = i == searchAxis ? ranges[i].getUpperBound() + 1 : ranges[i].getLowerBound();
							ubs[i] = i == searchAxis ? ranges[i].getUpperBound() + 1 : ranges[i].getUpperBound();
						}

						// Search cube contained?
						other.executeOperation(null, lbs, ubs, partOfSlice);

						if (partOfSlice.getResult()) {
							// Yes, enlarge our cube on that axis
							ranges[searchAxis].setUpperBound(ranges[searchAxis].getUpperBound() + 1);
						}

					} while (partOfSlice.getResult() && ranges[searchAxis].getUpperBound() < dimensions[searchAxis]);
				}

				// Mark the cube as visited and add to our result list
				for (int i = 0; i < n; i++) {
					lbs[i] = ranges[i].getLowerBound();
					ubs[i] = ranges[i].getUpperBound();
				}

				executeOperation(other, lbs, ubs, new ClearOperation());

				SliceCube cube = new SliceCube(ranges);
				cubes.add(cube);
			}
			return true;
		}

		public List<SliceCube> getCubes() {
			return cubes;
		}

	}

	private class ClearOperation implements Operation {

		@Override
		public boolean execute(SliceAdministrationCapability other, int[] coords) {
			set(coords, false);
			return true;
		}

	}

	/**
	 * Checks if a specific coordinate is part of a slice and if it's available.
	 */
	private class IsPartOfSliceOperation implements Operation {

		boolean	result	= true;

		@Override
		public boolean execute(SliceAdministrationCapability other, int[] coords) {

			this.result = (get(coords));

			return result;
		}

		public boolean getResult() {
			return result;
		}

		public void setResult(boolean result) {
			this.result = result;
		}

	}

	/**
	 * Executes a specific operation involving the {@link Slice} managed by this capability instance and the slice managed by the <code>other</code>
	 * {@link SliceAdministrationCapability} capability. This method is generic for n-dimensions slices, defined by the lenght of the arrays
	 * <code>lbs</code> and <code>ubs</code>
	 * 
	 * @param other
	 *            Capability managing the other slice involves in the operation.
	 * @param lbs
	 *            Array of length "n", where "n" defines the dimensions of the slice. Value lbs[i] contains the lower bound index of the i-axis.
	 * @param ubs
	 *            Array of length "n", where "n" defines the dimensions of the slice. Value ubs[i] contains the upper bound index of the i-axis.
	 * @param operation
	 *            Operation to be executed.
	 */
	private void executeOperation(SliceAdministrationCapability other, int[] lbs, int[] ubs, Operation operation) {
		int l = lbs.length;

		int[] c = Arrays.copyOfRange(lbs, 0, l);

		do {

			if (!operation.execute(other, c))
				return;

			int i = 0;
			c[i]++;
			while (i < l - 1 && c[i] > ubs[i]) {
				c[i] = lbs[i];
				i++;
				c[i]++;
			}
			// while the value is in range lbs <= x < ubs
		} while (c[l - 1] <= ubs[l - 1]);
	}

	private void compareSliceDefinition(SliceAdministrationCapability other) {

		log.debug("Comparing if both slices have same dimensions and same length for each slice units.");

		if (other.units.size() != this.units.size())
			throw new IllegalArgumentException("Only slices with same dimensions can be compared.");

		for (int i = 0; i < this.units.size(); i++)
			if (!this.units.get(i).equals(other.units.get(i)))
				throw new IllegalArgumentException("Slices units must be defined in same order.");

		switch (units.size()) {
			case 1:
				boolean d1[] = (boolean[]) currentData;
				boolean otherD1[] = (boolean[]) other.currentData;

				if (d1.length != otherD1.length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units.get(0));

				break;
			case 2:

				boolean d2[][] = (boolean[][]) currentData;
				boolean otherD2[][] = (boolean[][]) other.currentData;

				if (d2.length != otherD2.length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units.get(0));
				if (d2[0].length != otherD2[0].length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units.get(1));

				break;
			case 3:
				boolean d3[][][] = (boolean[][][]) currentData;
				boolean otherD3[][][] = (boolean[][][]) other.currentData;

				if (d3.length != otherD3.length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units.get(0));
				if (d3[0].length != otherD3[0].length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units.get(1));
				if (d3[0][0].length != otherD3[0][0].length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units.get(2));

				break;
			default:
				throw new RuntimeException(
						"Only up to three dimensions implemented");
		}

		log.debug("Slices comparison ended successfully.");

	}

	private SliceAdministrationCapability getSliceAdministration(IResource slice) throws SlicingException {
		try {
			return (SliceAdministrationCapability) serviceProvider.getCapability(slice, ISliceAdministrationCapability.class);
		} catch (CapabilityNotFoundException c) {
			throw new SlicingException("Error getting sliceAdministration capability from given resource:" + c.getMessage(), c);
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		switch (units.size()) {
			case 1:
				boolean d1[] = (boolean[]) currentData;

				for (int x = 0; x < d1.length; x++) {
					if (d1[x])
						sb.append("X");
					else
						sb.append("O");
				}
				break;
			case 2:
				boolean d2[][] = (boolean[][]) currentData;

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
				boolean d3[][][] = (boolean[][][]) currentData;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentData == null) ? 0 : currentData.hashCode());
		result = prime * result + ((originalData == null) ? 0 : originalData.hashCode());
		result = prime * result + ((sizes == null) ? 0 : sizes.hashCode());
		result = prime * result + ((units == null) ? 0 : units.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SliceAdministrationCapability other = (SliceAdministrationCapability) obj;
		if (currentData == null) {
			if (other.currentData != null)
				return false;
		} else if (!currentData.equals(other.currentData))
			return false;
		if (originalData == null) {
			if (other.originalData != null)
				return false;
		} else if (!originalData.equals(other.originalData))
			return false;
		if (sizes == null) {
			if (other.sizes != null)
				return false;
		} else if (!sizes.equals(other.sizes))
			return false;
		if (units == null) {
			if (other.units != null)
				return false;
		} else if (!units.equals(other.units))
			return false;
		return true;
	}

}
