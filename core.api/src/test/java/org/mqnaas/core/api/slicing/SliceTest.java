package org.mqnaas.core.api.slicing;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class SliceTest {

	private final static String	PORT_SLICE_UNIT_NAME	= "port";
	private final static String	TIME_SLICE_UNIT_NAME	= "time";
	private final static String	LAMBDA_SLICE_UNIT_NAME	= "lambda";

	@Test
	public void setSliceCube2DTest() {

		// define port-vlan slice
		SliceUnit[] units = new SliceUnit[2];
		units[0] = new SliceUnit("port");
		units[1] = new SliceUnit("vlan");

		// define 2x4 slice
		int[] sizes = new int[2];
		sizes[0] = 2;
		sizes[1] = 4;

		// initialize slice
		Slice slice = new Slice(units, sizes);

		// initialize cube : interfaces (0-1) and vlans (1-3)
		SliceCube cube = new SliceCube();
		Range[] ranges = new Range[2];
		ranges[0] = new Range(0, 1);
		ranges[1] = new Range(1, 3);
		cube.setRanges(ranges);

		// test and asserts
		slice.set(cube);

		int cords[] = new int[2];
		cords[0] = 0;
		cords[1] = 0;
		Assert.assertFalse("Position [0][0] of the slice should be false.", slice.get(cords));
		cords[1] = 1;
		Assert.assertTrue("Position [0][1] of the slice should be true.", slice.get(cords));
		cords[1] = 2;
		Assert.assertTrue("Position [0][2] of the slice should be true.", slice.get(cords));
		cords[1] = 3;
		Assert.assertTrue("Position [0][3] of the slice should be true.", slice.get(cords));
		cords[0] = 1;
		cords[1] = 0;
		Assert.assertFalse("Position [1][0] of the slice should be false.", slice.get(cords));
		cords[1] = 1;
		Assert.assertTrue("Position [1][1] of the slice should be true.", slice.get(cords));
		cords[1] = 2;
		Assert.assertTrue("Position [1][2] of the slice should be true.", slice.get(cords));
		cords[1] = 3;
		Assert.assertTrue("Position [1][3] of the slice should be true.", slice.get(cords));

		// test and assert to string
		StringBuilder sb = new StringBuilder();
		sb.append("OXXX").append("\n");
		sb.append("OXXX").append("\n");

		Assert.assertEquals("Slice representation should match the one stored in string builder.", sb.toString(), slice.toString());

		System.out.println("######################");
		System.out.println("## setSliceCubeTest ##");
		System.out.println("######################\n");
		System.out.println("Final slice:");
		System.out.println(slice.toString());
	}

	/**
	 * Test checks that {@link Slice#contains(Slice)} method fails if both slices have different number of dimensions (in this case (1D vs 2D)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void containsSliceDifferentDimensionsTest() {

		// define slice units
		SliceUnit[] originalUnits = new SliceUnit[1];
		SliceUnit[] otherUnits = new SliceUnit[2];

		int[] originalSize = { 2 };
		int[] otherSize = { 1, 3 };

		Slice originalSlice = new Slice(originalUnits, originalSize);
		Slice otherSlice = new Slice(otherUnits, otherSize);

		originalSlice.contains(otherSlice);

	}

	/**
	 * Test checks that {@link Slice#contains(Slice)} method fails if slice units are not defined in same order in both slices.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void containsSliceDifferentSliceOrder() {

		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);

		// we create inverse slice units arrays.
		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit };
		SliceUnit[] otherUnits = { timeSliceUnit, portSliceUnit };

		int[] originalSize = { 1, 3 };
		int[] otherSize = { 3, 1 };

		Slice originalSlice = new Slice(originalUnits, originalSize);
		Slice otherSlice = new Slice(otherUnits, otherSize);

		originalSlice.contains(otherSlice);

	}

	@Test
	public void constains3DTest() {

		// #########################
		// ## TEST INITIALIZATION ##
		// #########################

		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit, lambdaSliceUnit };
		SliceUnit[] otherUnits = { portSliceUnit, timeSliceUnit, lambdaSliceUnit };

		int[] size = { 2, 3, 4 };

		// #########################
		// ## TEST EXPECTING TRUE ##
		// #########################

		// initialize original cube : ports (0-1), time (0-1), slice(0-3)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);
		originalRanges[2] = new Range(0, 3);

		// initialize sub-cube, ports(0-1), time(0-1), slice(0-2). Should be contained in original one.
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 1);
		otherRanges[2] = new Range(0, 2);

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));
		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		Assert.assertTrue("OtherSice should be contained in original slice.", originalSlice.contains(otherSlice));

		// ##########################
		// ## TEST EXPECTING FALSE ##
		// ##########################

		// initialize sub-cube, ports(0-1), time(0-2), slice(0-3). Should not be contained in original one, since time[2] is not in originaLSlice.
		otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 2);
		otherRanges[2] = new Range(0, 3);

		otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		Assert.assertFalse("OtherSice should not be contained in original slice.", originalSlice.contains(otherSlice));

		// ###################################
		// ## TEST WITH SET OF CUBES - TRUE ##
		// ###################################

		// initialize sub-cubes: port(0), time(0), slice(0) and port(1), time(1), slice(2-3). Should be contained in original one.
		otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 0);
		otherRanges[1] = new Range(0, 0);
		otherRanges[2] = new Range(0, 0);

		Range[] anotherRanges = new Range[3];
		anotherRanges[0] = new Range(1, 1);
		anotherRanges[1] = new Range(1, 1);
		anotherRanges[2] = new Range(2, 3);

		otherSlice = new Slice(otherUnits, size);
		otherSlice.set(new SliceCube(otherRanges), new SliceCube(anotherRanges));

		Assert.assertTrue("OtherSice should be contained in original slice.", originalSlice.contains(otherSlice));

		// ####################################
		// ## TEST WITH SET OF CUBES - FALSE ##
		// ####################################

		// initialize sub-cubes, port(0), time(0), slice(0) and port(1), time(1-2), slice(2-3). Should not be contained in original one, since time[2]
		// is not in originaLSlice.

		otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 0);
		otherRanges[1] = new Range(0, 0);
		otherRanges[2] = new Range(0, 0);

		anotherRanges = new Range[3];
		anotherRanges[0] = new Range(1, 1);
		anotherRanges[1] = new Range(1, 2);
		anotherRanges[2] = new Range(2, 3);

		otherSlice = new Slice(otherUnits, size);
		otherSlice.set(new SliceCube(otherRanges), new SliceCube(anotherRanges));

		Assert.assertFalse("OtherSice should not be contained in original slice.", originalSlice.contains(otherSlice));

	}

	@Test
	public void contains2DTest() {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit };

		int[] size = { 3, 4 };

		// #########################
		// ## TEST EXPECTING TRUE ##
		// #########################

		// initialize original cube : ports (0-1), time (0-1),
		Range[] originalRanges = new Range[2];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);

		// initialize sub-cube, ports(0-1), time(0)Should be contained in original one.
		Range[] otherRanges = new Range[2];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 0);

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));
		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		Assert.assertTrue("OtherSice should be contained in original slice.", originalSlice.contains(otherSlice));

		// ##########################
		// ## TEST EXPECTING FALSE ##
		// ##########################

		// initialize sub-cube, ports(0-1), time(0-2) Should not be contained in original one, since time[2] is not in originaLSlice.
		otherRanges = new Range[2];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 2);

		otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		Assert.assertFalse("OtherSice should not be contained in original slice.", originalSlice.contains(otherSlice));

	}

	@Test
	public void contains1DTest() {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit };

		int[] size = { 4 };

		// #########################
		// ## TEST EXPECTING TRUE ##
		// #########################

		// initialize original cube : ports (0-2)
		Range[] originalRanges = new Range[1];
		originalRanges[0] = new Range(0, 2);

		// initialize sub-cube, ports(0-1). Should be contained in original one.
		Range[] otherRanges = new Range[1];
		otherRanges[0] = new Range(0, 1);

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));
		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		Assert.assertTrue("OtherSice should be contained in original slice.", originalSlice.contains(otherSlice));

		// ##########################
		// ## TEST EXPECTING FALSE ##
		// ##########################

		// initialize sub-cube, ports(0-3)
		otherRanges = new Range[1];
		otherRanges[0] = new Range(0, 3);

		otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges), new SliceCube(otherRanges));

		Assert.assertFalse("OtherSice should not be contained in original slice.", originalSlice.contains(otherSlice));

	}

	@Test(expected = SlicingException.class)
	public void addSliceAlreadyExistingValues3DTest() throws SlicingException {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit, lambdaSliceUnit };
		SliceUnit[] otherUnits = { portSliceUnit, timeSliceUnit, lambdaSliceUnit };

		int[] size = { 2, 3, 4 };

		Slice originalSlice = new Slice(originalUnits, size);
		Slice otherSlice = new Slice(otherUnits, size);

		// initialize original cube : ports (0-1), time (0-1), slice(0-3)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);
		originalRanges[2] = new Range(0, 3);

		// initialize sub-cube, ports(0-1), time(0-1), slice(0-2). Exists original one -> should fail with SlicingException
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 1);
		otherRanges[2] = new Range(0, 2);

		originalSlice.set(new SliceCube(originalRanges));
		otherSlice.set(new SliceCube(otherRanges));

		// call to method -> launch exception
		originalSlice.add(otherSlice);

	}

	@Test
	public void addSlice3DTest() throws SlicingException {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit, lambdaSliceUnit };

		int[] size = { 2, 3, 4 };

		// initialize original cube : ports (0-1), time (0-2), lambda(0-3)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 2);
		originalRanges[2] = new Range(0, 3);

		// initialize sub-cube, ports(0-1), time(2), lambda(0-3).
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(2, 2);
		otherRanges[2] = new Range(0, 3);

		System.out.println("####################");
		System.out.println("## addSlice3DTest ##");
		System.out.println("####################\n");

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));
		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		// we remove ports (0-1), time (2-2), lambda(0-3) from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(0, 1), new Range(2, 2), new Range(0, 3) };
		SliceCube cube = new SliceCube(ranges);
		originalSlice.unset(cube);

		System.out.println("Original Slice:");
		System.out.println(originalSlice);
		System.out.println("Slice to add :");
		System.out.println(otherSlice);

		// call to method
		originalSlice.add(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XXXX").append("\n");
		sb.append("XXXX").append("\n");
		sb.append("XXXX").append("\n");
		sb.append("\n");
		sb.append("XXXX").append("\n");
		sb.append("XXXX").append("\n");
		sb.append("XXXX").append("\n");
		sb.append("\n");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), originalSlice.toString());

		System.out.println("Final slice.");
		System.out.println(originalSlice);

	}

	@Test
	public void addSlice2DTest() throws SlicingException {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit };

		int[] size = { 3, 4 };

		// initialize original cube : ports (0-1), time (0-3)
		Range[] originalRanges = new Range[2];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 3);

		// initialize sub-cube, ports(0-1), time(2-3)
		Range[] otherRanges = new Range[2];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(2, 3);

		System.out.println("####################");
		System.out.println("## addSlice2DTest ##");
		System.out.println("####################\n");

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));
		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		// we remove ports (0-1), time (2-3) from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(0, 1), new Range(2, 3) };
		SliceCube cube = new SliceCube(ranges);
		originalSlice.unset(cube);

		System.out.println("Original Slice:");
		System.out.println(originalSlice);
		System.out.println("Slice to add :");
		System.out.println(otherSlice);

		// call to method
		originalSlice.add(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XXXX").append("\n");
		sb.append("XXXX").append("\n");
		sb.append("OOOO").append("\n");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), originalSlice.toString());

		System.out.println("Final slice.");
		System.out.println(originalSlice);

	}

	@Test
	public void addSlice1DTest() throws SlicingException {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit };

		int[] size = { 4 };

		// initialize original cube : ports (0-3)
		Range[] originalRanges = new Range[1];
		originalRanges[0] = new Range(0, 3);

		// initialize sub-cube, ports(3)
		Range[] otherRanges = new Range[1];
		otherRanges[0] = new Range(3, 3);

		System.out.println("####################");
		System.out.println("## addSlice1DTest ##");
		System.out.println("####################\n");

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));
		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		System.out.println("Current Slice:");
		System.out.println(originalSlice);
		System.out.println("Slice to add :");
		System.out.println(otherSlice);

		// we remove element[3] from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(3, 3) };
		SliceCube cube = new SliceCube(ranges);
		originalSlice.unset(cube);

		// call add method
		originalSlice.add(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XXXX");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), originalSlice.toString());

		System.out.println("Final slice.");
		System.out.println(originalSlice);

	}

	@Test
	public void addSliceNotContinousCubes3DTest() throws SlicingException {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit, lambdaSliceUnit };

		int[] size = { 2, 3, 4 };

		// initialize original slice with cube : ports (0-1), time (0-1), slice(0-3)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);
		originalRanges[2] = new Range(0, 3);

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));

		// initialize another slice with: cube : ports (0-1), time (0-1), slice(1,3)
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 1);
		otherRanges[2] = new Range(1, 1);

		Range[] secondOtherRanges = new Range[3];
		secondOtherRanges[0] = new Range(0, 1);
		secondOtherRanges[1] = new Range(0, 1);
		secondOtherRanges[2] = new Range(3, 3);

		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges), new SliceCube(secondOtherRanges));

		// we remove ports (0-1), time (0-1), lambda(1,3) from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(0, 1), new Range(0, 1), new Range(1, 1) };
		originalSlice.unset(new SliceCube(ranges));
		Range[] secondRanges = { new Range(0, 1), new Range(0, 1), new Range(3, 3) };
		originalSlice.unset(new SliceCube(secondRanges));

		System.out.println("#####################################");
		System.out.println("## addSliceNotContinousCubes3DTest ##");
		System.out.println("#####################################\n");

		System.out.println("Original Slice:");
		System.out.println(originalSlice);
		System.out.println("Slice to add :");
		System.out.println(otherSlice);
		// call to method
		originalSlice.add(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XXXX").append("\n");
		sb.append("XXXX").append("\n");
		sb.append("OOOO").append("\n");
		sb.append("\n");
		sb.append("XXXX").append("\n");
		sb.append("XXXX").append("\n");
		sb.append("OOOO").append("\n");
		sb.append("\n");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), originalSlice.toString());

		System.out.println("Final slice.");
		System.out.println(originalSlice);

	}

	@Test
	public void cutSlice3Dtest() throws SlicingException {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit, lambdaSliceUnit };

		int[] size = { 2, 3, 4 };

		// initialize original slice with cube : ports (0-1), time (0-1), slice(0-2)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);
		originalRanges[2] = new Range(0, 2);

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));

		// initialize another slice with: cube : ports (0-1), time (0-1), slice(1)
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 1);
		otherRanges[2] = new Range(1, 1);

		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		System.out.println("####################");
		System.out.println("## cutSlice3Dtest ##");
		System.out.println("####################\n");

		System.out.println("Original Slice:");
		System.out.println(originalSlice);
		System.out.println("Slice to cut :");
		System.out.println(otherSlice);

		// call to method
		originalSlice.cut(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XOXO").append("\n");
		sb.append("XOXO").append("\n");
		sb.append("OOOO").append("\n");
		sb.append("\n");
		sb.append("XOXO").append("\n");
		sb.append("XOXO").append("\n");
		sb.append("OOOO").append("\n");
		sb.append("\n");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), originalSlice.toString());

		System.out.println("Final slice.");
		System.out.println(originalSlice);

	}

	@Test
	public void cutSlice2DTest() throws SlicingException {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit };

		int[] size = { 3, 4 };

		// initialize original slice with cube : ports (0-1), time (0-1)
		Range[] originalRanges = new Range[2];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));

		// initialize another slice with: cube : ports (0-1), time (0)
		Range[] otherRanges = new Range[2];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 0);

		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		otherSlice.set(new SliceCube(otherRanges));

		System.out.println("####################");
		System.out.println("## cutSlice2DTest ##");
		System.out.println("####################\n");

		System.out.println("Original Slice:");
		System.out.println(originalSlice);
		System.out.println("Slice to cut :");
		System.out.println(otherSlice);

		// call to method
		originalSlice.cut(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("OXOO").append("\n");
		sb.append("OXOO").append("\n");
		sb.append("OOOO").append("\n");
		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), originalSlice.toString());

		System.out.println("Final slice.");
		System.out.println(originalSlice);

	}

	@Test
	public void cutSlice1DTest() throws SlicingException {
		// test initialization

		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit[] originalUnits = { portSliceUnit };

		int[] size = { 4 };

		// initialize original slice with cube : ports (0-2)
		Range[] originalRanges = new Range[1];
		originalRanges[0] = new Range(0, 2);

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges));

		// initialize another slice with: cube : ports (1-2)
		Range[] otherRanges = new Range[1];
		otherRanges[0] = new Range(1, 2);

		Slice otherSlice = new Slice(originalUnits, size, new SliceCube(otherRanges));

		System.out.println("####################");
		System.out.println("## cutSlice1DTest ##");
		System.out.println("####################\n");

		System.out.println("Original Slice:");
		System.out.println(originalSlice);
		System.out.println("Slice to cut :");
		System.out.println(otherSlice);

		// call to method
		originalSlice.cut(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XOOO");
		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), originalSlice.toString());

		System.out.println("Final slice.");
		System.out.println(originalSlice);

	}

	@Test
	public void cutSliceNotContinousCubes3DTest() throws SlicingException {

		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit timeSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, timeSliceUnit, lambdaSliceUnit };
		SliceUnit[] otherUnits = { portSliceUnit, timeSliceUnit, lambdaSliceUnit };

		int[] size = { 2, 3, 4 };

		// initialize original slice with cube : ports (0-1), time (0-1), slice(0,2)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);
		originalRanges[2] = new Range(0, 0);

		Range[] secondOriginalRanges = new Range[3];
		secondOriginalRanges[0] = new Range(0, 1);
		secondOriginalRanges[1] = new Range(0, 1);
		secondOriginalRanges[2] = new Range(2, 2);

		Slice originalSlice = new Slice(originalUnits, size, new SliceCube(originalRanges), new SliceCube(secondOriginalRanges));

		// initialize another slice with same cube : ports (0-1), time (0-1), slice(0,2)
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 1);
		otherRanges[2] = new Range(0, 0);

		Range[] secondOtherRanges = new Range[3];
		secondOtherRanges[0] = new Range(0, 1);
		secondOtherRanges[1] = new Range(0, 1);
		secondOtherRanges[2] = new Range(2, 2);

		Slice otherSlice = new Slice(otherUnits, size, new SliceCube(otherRanges), new SliceCube(secondOtherRanges));

		System.out.println("#####################################");
		System.out.println("## cutSliceNotContinousCubes3DTest ##");
		System.out.println("#####################################\n");

		System.out.println("Original Slice:");
		System.out.println(originalSlice);
		System.out.println("Slice to cut :");
		System.out.println(otherSlice);

		// call to method
		originalSlice.cut(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("OOOO").append("\n");
		sb.append("OOOO").append("\n");
		sb.append("OOOO").append("\n");
		sb.append("\n");
		sb.append("OOOO").append("\n");
		sb.append("OOOO").append("\n");
		sb.append("OOOO").append("\n");
		sb.append("\n");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), originalSlice.toString());

		System.out.println("Final slice.");
		System.out.println(originalSlice);

	}

	@Test
	@Ignore
	public void addSliceEfficiencyTest() throws SlicingException {
		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit vlanSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] units = { portSliceUnit, lambdaSliceUnit, vlanSliceUnit };

		int[] size = { 16, 16, 4096 };

		// original slize contains "false" for all fields. initialize another slice with all values to true
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		Slice originalSlice = new Slice(units, size, new SliceCube(originalRanges));
		Slice otherSlice = new Slice(units, size, new SliceCube(originalRanges));

		originalSlice.unset(new SliceCube(originalRanges));

		long startTime = System.currentTimeMillis();
		originalSlice.add(otherSlice);
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## addSliceEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}

	@Test
	@Ignore
	public void cutSliceEfficiencyTest() throws SlicingException {
		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit vlanSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] units = { portSliceUnit, lambdaSliceUnit, vlanSliceUnit };

		int[] size = { 16, 16, 4096 };

		// initialize both slices to true

		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		Slice originalSlice = new Slice(units, size, new SliceCube(originalRanges));
		Slice otherSlice = new Slice(units, size, new SliceCube(originalRanges));

		long startTime = System.currentTimeMillis();
		originalSlice.cut(otherSlice);
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## cutSliceEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}

	@Test
	@Ignore
	public void setEfficiencyTest() throws SlicingException {
		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit vlanSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] originalUnits = { portSliceUnit, lambdaSliceUnit, vlanSliceUnit };

		int[] size = { 16, 16, 4096 };

		Slice originalSlice = new Slice(originalUnits, size);

		// initialize both slices to true

		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		long startTime = System.currentTimeMillis();
		originalSlice.set(new SliceCube(originalRanges));
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## setEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}

	@Test
	@Ignore
	public void containsEfficiencyTest() throws SlicingException {
		// test initialization
		SliceUnit portSliceUnit = new SliceUnit(PORT_SLICE_UNIT_NAME);
		SliceUnit lambdaSliceUnit = new SliceUnit(TIME_SLICE_UNIT_NAME);
		SliceUnit vlanSliceUnit = new SliceUnit(LAMBDA_SLICE_UNIT_NAME);

		SliceUnit[] units = { portSliceUnit, lambdaSliceUnit, vlanSliceUnit };

		int[] size = { 16, 16, 4096 };

		// initialize both slices to true
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		Slice originalSlice = new Slice(units, size, new SliceCube(originalRanges));

		long startTime = System.currentTimeMillis();
		originalSlice.contains(originalSlice);
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## containsEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}
}