package org.mqnaas.core.api.slicing;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class Slice {

	private static final Logger	log	= LoggerFactory.getLogger(Slice.class);

	private SliceUnit[]			units;
	private Object				originalData;
	private Object				currentData;

	public Slice(SliceUnit[] units, int[] sizes, SliceCube... sliceCubes) {
		this.units = units;
		currentData = Array.newInstance(boolean.class, sizes);
		set(sliceCubes);

		cloneSlice();

	}

	private void cloneSlice() {
		switch (units.length) {
			case 1:
				originalData = SerializationUtils.clone((boolean[]) currentData);
				break;
			case 2:
				originalData = SerializationUtils.clone((boolean[][]) currentData);
				break;
			case 3:
				originalData = SerializationUtils.clone((boolean[][][]) currentData);
				break;
			default:
				throw new RuntimeException(
						"Only up to three dimensions implemented");
		}
	}

	/**
	 * @return the {@link SliceUnit slice units} of this slice.
	 */
	public SliceUnit[] getUnits() {
		return units;
	}

	/**
	 * Gets the boolean value of a specific field of the array, specified by the <code>coords</code> array.
	 * 
	 * @param coords
	 *            Position of the array which value we want to retrieve.
	 * @return The boolean value stores in this position.
	 * 
	 */
	public boolean get(int[] coords) {
		return get(currentData, coords);
	}

	private boolean get(Object data, int[] coords) {
		switch (units.length) {
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
	 * Checks if the given <code>other</code> {@link Slice} is contained in this one. A slice is contained in another slice if the cubes defining this
	 * slice do exists in the original one and are part of it.
	 * 
	 * @param other
	 *            Slice to be compared.
	 * @return <code>true</code> if the <code>other</code> slice is contained into this one. <code>false</code> otherwise.
	 */
	public boolean contains(Slice other) {

		compareSliceDefinition(other);

		int[] lbs = new int[units.length], ubs = new int[units.length];

		initUpperBounds(ubs);

		ContainsOperation contains = new ContainsOperation();
		executeOperation(other, lbs, ubs, contains);

		return contains.getResult();

	}

	/**
	 * 
	 * @param slice
	 * @throws SlicingException
	 */
	public void add(Slice other) throws SlicingException {

		log.info("Adding slice.");

		compareSliceDefinition(other);

		if (other.isInOperationalState())
			throw new SlicingException("ja");

		int[] lbs = new int[units.length], ubs = new int[units.length];

		initUpperBounds(ubs);

		NotContainsOperation preAdd = new NotContainsOperation();
		executeOperation(other, lbs, ubs, preAdd);

		if (!preAdd.getResult())
			throw new SlicingException("Given slice contains values that are already in the original slice.");

		AddOperation add = new AddOperation();
		executeOperation(other, lbs, ubs, add);

		log.info("Slice added");
	}

	/**
	 * 
	 * @param other
	 * @return
	 * @throws SlicingException
	 */
	public void cut(Slice other) throws SlicingException {

		log.info("Cutting slice");

		compareSliceDefinition(other);

		int[] lbs = new int[units.length], ubs = new int[units.length];

		initUpperBounds(ubs);

		ContainsOperation contains = new ContainsOperation();
		executeOperation(other, lbs, ubs, contains);

		if (!contains.getResult())
			throw new SlicingException("Given slice contains values that are not in the original slice.");

		CutOperation cut = new CutOperation();
		executeOperation(other, lbs, ubs, cut);

		log.info("Slice cut");

	}

	/**
	 * In the original slice cube, marks as used all the positions defined in the received {@link SliceCube slice cubes}.
	 * 
	 * @param cubes
	 *            Cubes to be marked as used.
	 */
	void set(SliceCube... cubes) {

		int[] lowerBounds = new int[units.length];
		int[] upperBounds = new int[units.length];

		for (SliceCube cube : cubes) {
			Range[] ranges = cube.getRanges();
			for (int i = 0; i < units.length; i++) {
				lowerBounds[i] = ranges[i].getLowerBound();
				upperBounds[i] = ranges[i].getUpperBound();
			}

			SetOperation set = new SetOperation();
			executeOperation(null, lowerBounds, upperBounds, set);

		}

	}

	void unset(SliceCube... cubes) {
		int[] lowerBounds = new int[units.length];
		int[] upperBounds = new int[units.length];

		for (SliceCube cube : cubes) {
			Range[] ranges = cube.getRanges();
			for (int i = 0; i < units.length; i++) {
				lowerBounds[i] = ranges[i].getLowerBound();
				upperBounds[i] = ranges[i].getUpperBound();
			}

			UnsetOperation set = new UnsetOperation();
			executeOperation(null, lowerBounds, upperBounds, set);
		}
	}

	/**
	 * Sets the <code>value</code> boolean value into the <code>data</code> array position defined by the <code>coords</code> array.
	 * 
	 * @param coords
	 *            Position of the <code>data</code> array defined by an array of indexes.
	 * @param value
	 *            boolean value to be set in this position.
	 */
	private void set(int[] coords, boolean value) {
		switch (units.length) {
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

	boolean isInOperationalState() {

		int[] lbs = new int[units.length], ubs = new int[units.length];
		initUpperBounds(ubs);

		int sizes[] = new int[ubs.length];
		for (int i = 0; i < ubs.length; i++)
			sizes[i] = ubs[i] + 1;

		Slice other = new Slice(units, sizes);
		other.currentData = this.originalData;

		ContainsOperation contains = new ContainsOperation();
		executeOperation(other, lbs, ubs, contains);

		return !contains.getResult();
	}

	private void compareSliceDefinition(Slice other) {

		log.debug("Comparing if both slices have same dimensions and same length for each slice units.");

		if (other.getUnits().length != this.units.length)
			throw new IllegalArgumentException("Only slices with same dimensions can be compared.");

		for (int i = 0; i < this.units.length; i++)
			if (!this.units[i].equals(other.units[i]))
				throw new IllegalArgumentException("Slices units must be defined in same order.");

		switch (units.length) {
			case 1:
				boolean d1[] = (boolean[]) currentData;
				boolean otherD1[] = (boolean[]) other.currentData;

				if (d1.length != otherD1.length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units[0]);

				break;
			case 2:

				boolean d2[][] = (boolean[][]) currentData;
				boolean otherD2[][] = (boolean[][]) other.currentData;

				if (d2.length != otherD2.length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units[0]);
				if (d2[0].length != otherD2[0].length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units[1]);

				break;
			case 3:
				boolean d3[][][] = (boolean[][][]) currentData;
				boolean otherD3[][][] = (boolean[][][]) other.currentData;

				if (d3.length != otherD3.length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units[0]);
				if (d3[0].length != otherD3[0].length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units[1]);
				if (d3[0][0].length != otherD3[0][0].length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units[2]);

				break;
			default:
				throw new RuntimeException(
						"Only up to three dimensions implemented");
		}

		log.debug("Slices comparison ended successfully.");

	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();

		switch (units.length) {
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

	private void initUpperBounds(int[] ubs) {
		Object it = currentData;

		for (int i = 0; i < units.length; i++) {
			ubs[i] = Array.getLength(it) - 1;
			it = Array.get(it, 0);
		}
	}

	private void executeOperation(Slice other, int[] lbs, int[] ubs, Operation operation) {
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

	private interface Operation {

		boolean execute(Slice other, int[] coords);
	}

	private class ContainsOperation implements Operation {

		private boolean	result	= true;

		@Override
		public boolean execute(Slice other, int[] coords) {
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

	private class NotContainsOperation implements Operation {

		private boolean	result	= true;

		@Override
		public boolean execute(Slice other, int[] coords) {
			if (other.get(coords) && get(coords))
				result = false;

			return result;

		}

		public boolean getResult() {
			return result;
		}

	}

	private class AddOperation implements Operation {

		@Override
		public boolean execute(Slice other, int[] coords) {
			// we can only add elements that were part of the original slice.
			if (other.get(coords) && get(originalData, coords))
				set(coords, true);

			return true;

		}

	}

	private class CutOperation implements Operation {

		@Override
		public boolean execute(Slice other, int[] coords) {
			if (other.get(coords))
				set(coords, false);
			return true;
		}

	}

	private class SetOperation implements Operation {

		@Override
		public boolean execute(Slice other, int[] coords) {
			set(coords, true);

			return true;
		}

	}

	private class UnsetOperation implements Operation {

		@Override
		public boolean execute(Slice other, int[] coords) {
			set(coords, false);

			return true;
		}

	}

}
