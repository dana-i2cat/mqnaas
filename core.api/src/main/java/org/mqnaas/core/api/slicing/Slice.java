package org.mqnaas.core.api.slicing;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private Object				data;

	public Slice(SliceUnit[] units, int[] sizes) {
		this.units = units;
		data = Array.newInstance(boolean.class, sizes);
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

		switch (units.length) {
			case 1:
				boolean d1[] = (boolean[]) data;
				boolean otherD1[] = (boolean[]) other.data;

				for (int i = 0; i < d1.length; i++)
					if (otherD1[i] && !d1[i])
						return false;
				break;

			case 2:
				boolean d2[][] = (boolean[][]) data;
				boolean otherD2[][] = (boolean[][]) other.data;

				for (int i = 0; i < d2.length; i++)
					for (int j = 0; j < d2.length; j++)
						if (otherD2[i][j] && !d2[i][j])
							return false;

				break;

			case 3:

				boolean d3[][][] = (boolean[][][]) data;
				boolean otherD3[][][] = (boolean[][][]) other.data;

				for (int i = 0; i < d3.length; i++)
					for (int j = 0; j < d3[0].length; j++)
						for (int k = 0; k < d3[0][0].length; k++)
							if (otherD3[i][j][k] && !d3[i][j][k])
								return false;

				break;

			default:
				throw new RuntimeException(
						"Only up to three dimensions implemented");
		}

		return true;
	}

	/**
	 * 
	 * @param slice
	 * @throws SlicingException
	 */
	public void add(Slice other) throws SlicingException {

		log.info("Adding slice.");

		compareSliceDefinition(other);

		// FIXME it build slicecubes of length 1 for each dimension. We have to improve it.
		List<SliceCube> cubes = new ArrayList<SliceCube>();

		switch (units.length) {
			case 1:
				boolean d1[] = (boolean[]) data;
				boolean otherD1[] = (boolean[]) other.data;

				for (int i = 0; i < d1.length; i++)
					if (otherD1[i] && d1[i])
						throw new SlicingException("Given slice contains values that are already in the original slice.");
					else if (otherD1[i] && !d1[i]) {
						Range[] ranges = { new Range(i, i) };
						SliceCube cube = new SliceCube(ranges);
						cubes.add(cube);
					}

				break;

			case 2:
				boolean d2[][] = (boolean[][]) data;
				boolean otherD2[][] = (boolean[][]) other.data;

				for (int i = 0; i < d2.length; i++)
					for (int j = 0; j < d2.length; j++)
						if (otherD2[i][j] && d2[i][j])
							throw new SlicingException("Given slice contains values that are already in the original slice.");
						else if (otherD2[i][j] && !d2[i][j]) {
							Range[] ranges = { new Range(i, i), new Range(j, j) };
							SliceCube cube = new SliceCube(ranges);
							cubes.add(cube);
						}
				break;

			case 3:

				boolean d3[][][] = (boolean[][][]) data;
				boolean otherD3[][][] = (boolean[][][]) other.data;

				for (int i = 0; i < d3.length; i++)
					for (int j = 0; j < d3[0].length; j++)
						for (int k = 0; k < d3[0][0].length; k++)
							if (otherD3[i][j][k] && d3[i][j][k])
								throw new SlicingException("Given slice contains values that are already in the original slice.");
							else if (otherD3[i][j][k] && !d3[i][j][k]) {
								Range[] ranges = { new Range(i, i), new Range(j, j), new Range(k, k) };
								SliceCube cube = new SliceCube(ranges);
								cubes.add(cube);
							}
				break;

			default:
				throw new RuntimeException(
						"Only up to three dimensions implemented");

		}

		set(cubes.toArray(new SliceCube[cubes.size()]));

		log.info("Slice added");
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

			set(lowerBounds, upperBounds, true);
		}

	}

	/**
	 * Sets the boolean <code>v</code> value in all positions of the slice for each index between the ranges indicated in the arrays of
	 * <code>lbs</code> and <code>ubs</code>
	 * 
	 * @param lbs
	 *            Array containing the lower bounds of the different ranges of slice units.
	 * @param ubs
	 *            Array containing the upper bounds of the different ranges of slices units.
	 * @param v
	 *            boolean value to be set in these ranges.
	 */
	private void set(int[] lbs, int[] ubs, boolean v) {
		int l = lbs.length;

		int[] c = Arrays.copyOfRange(lbs, 0, l);

		do {
			set(c, v);

			int i = 0;
			c[i]++;
			while (i < l - 1 && c[i] > ubs[i]) {
				c[i] = lbs[i];
				i++;
				c[i]++;
			}
		} while (c[l - 1] <= ubs[l - 1]);
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
				boolean d1[] = (boolean[]) data;
				d1[coords[0]] = value;
				break;
			case 2:
				boolean d2[][] = (boolean[][]) data;
				d2[coords[0]][coords[1]] = value;
				break;
			case 3:
				boolean d3[][][] = (boolean[][][]) data;
				d3[coords[0]][coords[1]][coords[2]] = value;
				break;
			default:
				throw new RuntimeException(
						"Only up to three dimensions implemented");
		}
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
				boolean d1[] = (boolean[]) data;
				boolean otherD1[] = (boolean[]) other.data;

				if (d1.length != otherD1.length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units[0]);

				break;
			case 2:

				boolean d2[][] = (boolean[][]) data;
				boolean otherD2[][] = (boolean[][]) other.data;

				if (d2.length != otherD2.length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units[0]);
				if (d2[0].length != otherD2.length)
					throw new IllegalArgumentException("Slices have different size for slice unit " + this.units[1]);

				break;
			case 3:
				boolean d3[][][] = (boolean[][][]) data;
				boolean otherD3[][][] = (boolean[][][]) other.data;

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
				boolean d1[] = (boolean[]) data;

				for (int x = 0; x < d1.length; x++) {
					if (d1[x])
						sb.append("X");
					else
						sb.append("O");
				}
				break;
			case 2:
				boolean d2[][] = (boolean[][]) data;

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
				boolean d3[][][] = (boolean[][][]) data;

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
