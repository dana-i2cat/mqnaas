package org.mqnaas.core.impl.slicing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.api.slicing.SliceCube;
import org.mqnaas.core.api.slicing.SlicingException;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.powermock.api.mockito.PowerMockito;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 *
 */
public class SliceAdministrationCapabilityTest {

	private final static String		PORT_SLICE_UNIT_NAME	= "port";
	private final static String		TIME_SLICE_UNIT_NAME	= "time";
	private final static String		LAMBDA_SLICE_UNIT_NAME	= "lambda";

	SliceAdministrationCapability	sliceCapab;
	SliceAdministrationCapability	otherSliceCapab;
	IResource						slice;
	IResource						otherSlice;

	@SuppressWarnings("unchecked")
	@Before
	public void prepareTest() throws Exception {

		slice = new Slice();
		otherSlice = new Slice();

		IServiceProvider serviceProvider = Mockito.mock(IServiceProvider.class);

		sliceCapab = PowerMockito.spy(new SliceAdministrationCapability());
		otherSliceCapab = new SliceAdministrationCapability();

		sliceCapab.activate();
		otherSliceCapab.activate();

		Mockito.when(serviceProvider.getCapability(Mockito.any(IResource.class), Mockito.any(Class.class))).thenReturn(otherSliceCapab);

		ReflectionTestHelper.injectPrivateField(sliceCapab, serviceProvider, "serviceProvider");
	}

	@Test
	public void setSliceCube2DTest() {

		// initialize sliceAdministrationCapability, define port-time slice with 2x4 dimensions.
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 4);

		// initialize cube : interfaces (0-1) and vlans (1-3)
		SliceCube cube = new SliceCube();
		Range[] ranges = new Range[2];
		ranges[0] = new Range(0, 1);
		ranges[1] = new Range(1, 3);
		cube.setRanges(ranges);

		// test and asserts
		sliceCapab.setCubes(Arrays.asList(cube));

		int cords[] = new int[2];
		cords[0] = 0;
		cords[1] = 0;
		Assert.assertFalse("Position [0][0] of the slice should be false.", sliceCapab.get(cords));
		cords[1] = 1;
		Assert.assertTrue("Position [0][1] of the slice should be true.", sliceCapab.get(cords));
		cords[1] = 2;
		Assert.assertTrue("Position [0][2] of the slice should be true.", sliceCapab.get(cords));
		cords[1] = 3;
		Assert.assertTrue("Position [0][3] of the slice should be true.", sliceCapab.get(cords));
		cords[0] = 1;
		cords[1] = 0;
		Assert.assertFalse("Position [1][0] of the slice should be false.", sliceCapab.get(cords));
		cords[1] = 1;
		Assert.assertTrue("Position [1][1] of the slice should be true.", sliceCapab.get(cords));
		cords[1] = 2;
		Assert.assertTrue("Position [1][2] of the slice should be true.", sliceCapab.get(cords));
		cords[1] = 3;
		Assert.assertTrue("Position [1][3] of the slice should be true.", sliceCapab.get(cords));

		// test and assert to string
		StringBuilder sb = new StringBuilder();
		sb.append("OXXX").append("\n");
		sb.append("OXXX").append("\n");

		Assert.assertEquals("Slice representation should match the one stored in string builder.", sb.toString(), sliceCapab.toString());

		System.out.println("######################");
		System.out.println("## setSliceCubeTest ##");
		System.out.println("######################\n");
		System.out.println("Final slice:");
		System.out.println(sliceCapab.toString());
	}

	/**
	 * Test checks that {@link OldSlice#contains(OldSlice)} method fails if both slices have different number of dimensions (in this case (1D vs 2D)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void containsSliceDifferentDimensionsTest() throws Exception {

		// initialize sliceAdministrationCapability, define port slice with size 2
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);

		// initialize another sliceAdministrationCapability, define port-time slice with 2x3 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);

		sliceCapab.contains(otherSlice);

	}

	//
	/**
	 * Test checks that {@link OldSlice#contains(OldSlice)} method fails if slice units are not defined in same order in both slices.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void containsSliceDifferentSliceOrder() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);

		// initialize sliceAdministrationCapability, define inverse slice: time-port
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);

		sliceCapab.contains(otherSlice);

	}

	@Test
	@Ignore
	public void constains3DTest() throws SlicingException {

		// #########################
		// ## TEST INITIALIZATION ##
		// #########################

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

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

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		Assert.assertTrue("OtherSice should be contained in original slice.", sliceCapab.contains(otherSlice));

		// ##########################
		// ## TEST EXPECTING FALSE ##
		// ##########################

		otherSliceCapab.originalData = null;
		otherSliceCapab.currentData = null;

		// initialize sub-cube, ports(0-1), time(0-2), slice(0-3). Should not be contained in original one, since time[2] is not in originaLSlice.
		otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 2);
		otherRanges[2] = new Range(0, 3);

		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		Assert.assertFalse("OtherSice should not be contained in original slice.", sliceCapab.contains(otherSlice));

		// ###################################
		// ## TEST WITH SET OF CUBES - TRUE ##
		// ###################################

		otherSliceCapab.originalData = null;
		otherSliceCapab.currentData = null;
		// initialize sub-cubes: port(0), time(0), slice(0) and port(1), time(1), slice(2-3). Should be contained in original one.
		otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 0);
		otherRanges[1] = new Range(0, 0);
		otherRanges[2] = new Range(0, 0);

		Range[] anotherRanges = new Range[3];
		anotherRanges[0] = new Range(1, 1);
		anotherRanges[1] = new Range(1, 1);
		anotherRanges[2] = new Range(2, 3);

		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges), new SliceCube(anotherRanges)));

		Assert.assertTrue("OtherSice should be contained in original slice.", sliceCapab.contains(otherSlice));

		// ####################################
		// ## TEST WITH SET OF CUBES - FALSE ##
		// ####################################

		// initialize sub-cubes, port(0), time(0), slice(0) and port(1), time(1-2), slice(2-3). Should not be contained in original one, since time[2]
		// is not in originaLSlice.

		otherSliceCapab.originalData = null;
		otherSliceCapab.currentData = null;

		otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 0);
		otherRanges[1] = new Range(0, 0);
		otherRanges[2] = new Range(0, 0);

		anotherRanges = new Range[3];
		anotherRanges[0] = new Range(1, 1);
		anotherRanges[1] = new Range(1, 2);
		anotherRanges[2] = new Range(2, 3);

		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges), new SliceCube(anotherRanges)));

		Assert.assertFalse("OtherSice should not be contained in original slice.", sliceCapab.contains(otherSlice));

	}

	@Test
	public void contains1DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port slice of size 4
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 4);
		// initialize another sliceAdministrationCapability, define port slice of size 4
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 4);

		// #########################
		// ## TEST EXPECTING TRUE ##
		// #########################

		// initialize original cube : ports (0-2)
		Range[] originalRanges = new Range[1];
		originalRanges[0] = new Range(0, 2);

		// initialize sub-cube, ports(0-1). Should be contained in original one.
		Range[] otherRanges = new Range[1];
		otherRanges[0] = new Range(0, 1);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		Assert.assertTrue("OtherSice should be contained in original slice.", sliceCapab.contains(otherSlice));

		// ##########################
		// ## TEST EXPECTING FALSE ##
		// ##########################

		otherSliceCapab.originalData = null;
		otherSliceCapab.currentData = null;

		// initialize sub-cube, ports(0-3)
		otherRanges = new Range[1];
		otherRanges[0] = new Range(0, 3);

		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		Assert.assertFalse("OtherSice should not be contained in original slice.", sliceCapab.contains(otherSlice));

	}

	@Test
	public void contains2DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time 3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 4);
		// initialize another sliceAdministrationCapability, define port slice of size 4
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 4);

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

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		Assert.assertTrue("OtherSice should be contained in original slice.", sliceCapab.contains(otherSlice));

		// ##########################
		// ## TEST EXPECTING FALSE ##
		// ##########################

		otherSliceCapab.originalData = null;
		otherSliceCapab.currentData = null;

		// initialize sub-cube, ports(0-1), time(0-2) Should not be contained in original one, since time[2] is not in originaLSlice.
		otherRanges = new Range[2];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 2);

		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		Assert.assertFalse("OtherSice should not be contained in original slice.", sliceCapab.contains(otherSlice));

	}

	@Test(expected = SlicingException.class)
	public void addSliceAlreadyExistingValues3DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

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

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		// call to method -> launch exception
		sliceCapab.add(otherSlice);

	}

	@Test
	public void addSlice3DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

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

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		// we remove ports (0-1), time (2-2), lambda(0-3) from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(0, 1), new Range(2, 2), new Range(0, 3) };
		SliceCube cube = new SliceCube(ranges);
		sliceCapab.unset(cube);

		System.out.println("Original Slice:");
		System.out.println(sliceCapab);
		System.out.println("Slice to add :");
		System.out.println(otherSliceCapab);

		// call to method
		sliceCapab.add(otherSlice);

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

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), sliceCapab.toString());

		System.out.println("Final slice.");
		System.out.println(sliceCapab);

	}

	@Test
	public void addSlice2DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time 3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time 3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 4);

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

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		// we remove ports (0-1), time (2-3) from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(0, 1), new Range(2, 3) };
		SliceCube cube = new SliceCube(ranges);
		sliceCapab.unset(cube);

		System.out.println("Original Slice:");
		System.out.println(sliceCapab);
		System.out.println("Slice to add :");
		System.out.println(otherSliceCapab);

		// call to method
		sliceCapab.add(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XXXX").append("\n");
		sb.append("XXXX").append("\n");
		sb.append("OOOO").append("\n");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), sliceCapab.toString());

		System.out.println("Final slice.");
		System.out.println(sliceCapab);

	}

	@Test
	public void addSlice1DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port slice of size 4
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 4);
		// initialize another sliceAdministrationCapability, define port slize of size 4
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 4);

		// initialize original cube : ports (0-3)
		Range[] originalRanges = new Range[1];
		originalRanges[0] = new Range(0, 3);

		// initialize sub-cube, ports(3)
		Range[] otherRanges = new Range[1];
		otherRanges[0] = new Range(3, 3);

		System.out.println("####################");
		System.out.println("## addSlice1DTest ##");
		System.out.println("####################\n");

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		System.out.println("Current Slice:");
		System.out.println(sliceCapab);
		System.out.println("Slice to add :");
		System.out.println(otherSliceCapab);

		// we remove element[3] from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(3, 3) };
		SliceCube cube = new SliceCube(ranges);
		sliceCapab.unset(cube);

		// call add method
		sliceCapab.add(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XXXX");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), sliceCapab.toString());

		System.out.println("Final slice.");
		System.out.println(sliceCapab);

	}

	@Test
	public void addSliceNotContinousCubes3DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize original slice with cube : ports (0-1), time (0-1), slice(0-3)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);
		originalRanges[2] = new Range(0, 3);

		// initialize another slice with: cube : ports (0-1), time (0-1), slice(1,3)
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 1);
		otherRanges[2] = new Range(1, 1);

		Range[] secondOtherRanges = new Range[3];
		secondOtherRanges[0] = new Range(0, 1);
		secondOtherRanges[1] = new Range(0, 1);
		secondOtherRanges[2] = new Range(3, 3);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges), new SliceCube(secondOtherRanges)));

		// we remove ports (0-1), time (0-1), lambda(1,3) from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(0, 1), new Range(0, 1), new Range(1, 1) };
		sliceCapab.unset(new SliceCube(ranges));
		Range[] secondRanges = { new Range(0, 1), new Range(0, 1), new Range(3, 3) };
		sliceCapab.unset(new SliceCube(secondRanges));

		System.out.println("#####################################");
		System.out.println("## addSliceNotContinousCubes3DTest ##");
		System.out.println("#####################################\n");

		System.out.println("Original Slice:");
		System.out.println(sliceCapab);
		System.out.println("Slice to add :");
		System.out.println(otherSliceCapab);
		// call to method
		sliceCapab.add(otherSlice);

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

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), sliceCapab.toString());

		System.out.println("Final slice.");
		System.out.println(sliceCapab);

	}

	@Test
	public void cutSlice3Dtest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize original slice with cube : ports (0-1), time (0-1), slice(0-2)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);
		originalRanges[2] = new Range(0, 2);

		// initialize another slice with: cube : ports (0-1), time (0-1), slice(1)
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 1);
		otherRanges[2] = new Range(1, 1);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		System.out.println("####################");
		System.out.println("## cutSlice3Dtest ##");
		System.out.println("####################\n");

		System.out.println("Original Slice:");
		System.out.println(sliceCapab);
		System.out.println("Slice to cut :");
		System.out.println(otherSliceCapab);

		// call to method
		sliceCapab.cut(otherSlice);

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

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), sliceCapab.toString());

		System.out.println("Final slice.");
		System.out.println(sliceCapab);

	}

	@Test
	public void cutSlice2DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time 3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time 3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 4);

		// initialize original slice with cube : ports (0-1), time (0-1)
		Range[] originalRanges = new Range[2];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);

		// initialize another slice with: cube : ports (0-1), time (0)
		Range[] otherRanges = new Range[2];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 0);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		System.out.println("####################");
		System.out.println("## cutSlice2DTest ##");
		System.out.println("####################\n");

		System.out.println("Original Slice:");
		System.out.println(sliceCapab);
		System.out.println("Slice to cut :");
		System.out.println(otherSliceCapab);

		// call to method
		sliceCapab.cut(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("OXOO").append("\n");
		sb.append("OXOO").append("\n");
		sb.append("OOOO").append("\n");
		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), sliceCapab.toString());

		System.out.println("Final slice.");
		System.out.println(sliceCapab);

	}

	@Test
	public void cutSlice1DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port slice of size 4
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port slice of size 4
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 4);

		// initialize original slice with cube : ports (0-2)
		Range[] originalRanges = new Range[1];
		originalRanges[0] = new Range(0, 2);

		// initialize another slice with: cube : ports (1-2)
		Range[] otherRanges = new Range[1];
		otherRanges[0] = new Range(1, 2);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges)));

		System.out.println("####################");
		System.out.println("## cutSlice1DTest ##");
		System.out.println("####################\n");

		System.out.println("Original Slice:");
		System.out.println(sliceCapab);
		System.out.println("Slice to cut :");
		System.out.println(otherSliceCapab);

		// call to method
		sliceCapab.cut(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XOOO");
		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), sliceCapab.toString());

		System.out.println("Final slice.");
		System.out.println(sliceCapab);

	}

	@Test
	public void cutSliceNotContinousCubes3DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize original slice with cube : ports (0-1), time (0-1), slice(0,2)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);
		originalRanges[2] = new Range(0, 0);

		Range[] secondOriginalRanges = new Range[3];
		secondOriginalRanges[0] = new Range(0, 1);
		secondOriginalRanges[1] = new Range(0, 1);
		secondOriginalRanges[2] = new Range(2, 2);

		// initialize another slice with same cube : ports (0-1), time (0-1), slice(0,2)
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 1);
		otherRanges[2] = new Range(0, 0);

		Range[] secondOtherRanges = new Range[3];
		secondOtherRanges[0] = new Range(0, 1);
		secondOtherRanges[1] = new Range(0, 1);
		secondOtherRanges[2] = new Range(2, 2);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges), new SliceCube(secondOriginalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(otherRanges), new SliceCube(secondOtherRanges)));

		System.out.println("#####################################");
		System.out.println("## cutSliceNotContinousCubes3DTest ##");
		System.out.println("#####################################\n");

		System.out.println("Original Slice:");
		System.out.println(sliceCapab);
		System.out.println("Slice to cut :");
		System.out.println(otherSliceCapab);

		// call to method
		sliceCapab.cut(otherSlice);

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

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), sliceCapab.toString());

		System.out.println("Final slice.");
		System.out.println(sliceCapab);

	}

	@Test
	public void isInOperationalStateTest() {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize original slice with cube : ports (0-1), time (0-1), slice(0,0)
		Range[] ranges = new Range[3];
		ranges[0] = new Range(0, 1);
		ranges[1] = new Range(0, 1);
		ranges[2] = new Range(0, 0);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(ranges)));

		Assert.assertFalse("Slice should not be in operational state, since it contains same current and original data.",
				sliceCapab.isInOperationalState());

		ranges = new Range[3];
		ranges[0] = new Range(0, 0);
		ranges[1] = new Range(0, 1);
		ranges[2] = new Range(0, 0);
		sliceCapab.unset(new SliceCube(ranges));

		Assert.assertTrue("Slice should  be in operational state, since it does not contain same current and original data.",
				sliceCapab.isInOperationalState());

	}

	@Test
	public void getCubes1DTest() {

		// initialize sliceAdministrationCapability, define port slice of 16 size
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 16);

		// initialize slices 0-3 and 10-12
		Range[] ranges = { new Range(0, 3) };
		Range[] secondRanges = { new Range(10, 12) };

		sliceCapab.setCubes(Arrays.asList(new SliceCube(ranges), new SliceCube(secondRanges)));

		Collection<SliceCube> cubes = sliceCapab.getCubes();

		Assert.assertEquals("There should be 2 different slice cubes.", 2, cubes.size());

		List<SliceCube> cubeList = new ArrayList<SliceCube>(cubes);

		SliceCube firstCube = cubeList.get(0);
		SliceCube secondCube = cubeList.get(1);

		Assert.assertNotNull(firstCube);
		Assert.assertNotNull(secondCube);

		Assert.assertTrue(firstCube.getRanges().length == 1);
		Assert.assertTrue(secondCube.getRanges().length == 1);

		Assert.assertTrue("First cube should contain as lower bound port 0.", firstCube.getRanges()[0].getLowerBound() == 0);
		Assert.assertTrue("First cube should contain as upper bound port 0.", firstCube.getRanges()[0].getUpperBound() == 3);

		Assert.assertTrue("First cube should contain as lower bound port 10.", secondCube.getRanges()[0].getLowerBound() == 10);
		Assert.assertTrue("First cube should contain as upper bound port 12.", secondCube.getRanges()[0].getUpperBound() == 12);
	}

	@Test
	public void getCubes3DTest() {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize original cube : ports (0-1), time (0-2), lambda(0-2)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 2);
		originalRanges[2] = new Range(0, 2);

		// initialize another cube, ports(0), time(0), lambda(3).
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 0);
		otherRanges[1] = new Range(0, 0);
		otherRanges[2] = new Range(3, 3);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges), new SliceCube(otherRanges)));

		Collection<SliceCube> cubes = sliceCapab.getCubes();

		Assert.assertEquals("There should be 2 different slice cubes.", 2, cubes.size());

		List<SliceCube> cubeList = new ArrayList<SliceCube>(cubes);

		SliceCube firstCube = cubeList.get(0);
		SliceCube secondCube = cubeList.get(1);

		Assert.assertNotNull(firstCube);
		Assert.assertNotNull(secondCube);

		Assert.assertTrue(firstCube.getRanges().length == 3);
		Assert.assertTrue(secondCube.getRanges().length == 3);

		Assert.assertTrue("First cube should contain as lower bound port 0.", firstCube.getRanges()[0].getLowerBound() == 0);
		Assert.assertTrue("First cube should contain as upper bound port 1.", firstCube.getRanges()[0].getUpperBound() == 1);
		Assert.assertTrue("First cube should contain as lower bound time 0.", firstCube.getRanges()[1].getLowerBound() == 0);
		Assert.assertTrue("First cube should contain as upper bound time 2.", firstCube.getRanges()[1].getUpperBound() == 2);
		Assert.assertTrue("First cube should contain as lower bound lambda 0.", firstCube.getRanges()[2].getLowerBound() == 0);
		Assert.assertTrue("First cube should contain as upper bound lambda 2.", firstCube.getRanges()[2].getUpperBound() == 2);

		Assert.assertTrue("Second cube should contain as lower bound port 0.", secondCube.getRanges()[0].getLowerBound() == 0);
		Assert.assertTrue("Second cube should contain as upper bound port 0.", secondCube.getRanges()[0].getUpperBound() == 0);
		Assert.assertTrue("Second cube should contain as lower bound time 0.", secondCube.getRanges()[1].getLowerBound() == 0);
		Assert.assertTrue("Second cube should contain as upper bound time 0.", secondCube.getRanges()[1].getUpperBound() == 0);
		Assert.assertTrue("Second cube should contain as lower bound lambda 3.", secondCube.getRanges()[2].getLowerBound() == 3);
		Assert.assertTrue("Second cube should contain as upper bound lambda 3.", secondCube.getRanges()[2].getUpperBound() == 3);

	}

	@Test
	public void getAvailableCubes3Dtest() {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 2);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 3);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4);

		// initialize slice with all cubes available
		Range[] wholeRange = new Range[3];
		wholeRange[0] = new Range(0, 1);
		wholeRange[1] = new Range(0, 2);
		wholeRange[2] = new Range(0, 3);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(wholeRange)));

		// unset cube : ports (0-1), time (0-2), lambda(0-2)
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 2);
		originalRanges[2] = new Range(0, 2);

		// unset cube, ports(0), time(0), lambda(3).
		Range[] otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 0);
		otherRanges[1] = new Range(0, 0);
		otherRanges[2] = new Range(3, 3);

		sliceCapab.unset(new SliceCube(originalRanges), new SliceCube(otherRanges));

		Collection<SliceCube> cubes = sliceCapab.getAvailableCubes();

		Assert.assertEquals("There should be 2 different slice cubes.", 2, cubes.size());

		List<SliceCube> cubeList = new ArrayList<SliceCube>(cubes);

		SliceCube firstCube = cubeList.get(0);
		SliceCube secondCube = cubeList.get(1);

		Assert.assertNotNull(firstCube);
		Assert.assertNotNull(secondCube);

		Assert.assertTrue(firstCube.getRanges().length == 3);
		Assert.assertTrue(secondCube.getRanges().length == 3);

		Assert.assertTrue("First cube should contain as lower bound port 1.", firstCube.getRanges()[0].getLowerBound() == 1);
		Assert.assertTrue("First cube should contain as upper bound port 1.", firstCube.getRanges()[0].getUpperBound() == 1);
		Assert.assertTrue("First cube should contain as lower bound time 0.", firstCube.getRanges()[1].getLowerBound() == 0);
		Assert.assertTrue("First cube should contain as upper bound time 2.", firstCube.getRanges()[1].getUpperBound() == 2);
		Assert.assertTrue("First cube should contain as lower bound lambda 3.", firstCube.getRanges()[2].getLowerBound() == 3);
		Assert.assertTrue("First cube should contain as upper bound lambda 3.", firstCube.getRanges()[2].getUpperBound() == 3);

		Assert.assertTrue("Second cube should contain as lower bound port 0.", secondCube.getRanges()[0].getLowerBound() == 0);
		Assert.assertTrue("Second cube should contain as upper bound port 0.", secondCube.getRanges()[0].getUpperBound() == 0);
		Assert.assertTrue("Second cube should contain as lower bound time 0.", secondCube.getRanges()[1].getLowerBound() == 1);
		Assert.assertTrue("Second cube should contain as upper bound time 0.", secondCube.getRanges()[1].getUpperBound() == 2);
		Assert.assertTrue("Second cube should contain as lower bound lambda 3.", secondCube.getRanges()[2].getLowerBound() == 3);
		Assert.assertTrue("Second cube should contain as upper bound lambda 3.", secondCube.getRanges()[2].getUpperBound() == 3);

	}

	@Test
	public void getAvailableCubes1DTest() {

		// initialize sliceAdministrationCapability, define port slice of 16 size
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 16);

		// initialize whole slice.
		Range[] ranges = { new Range(0, 15) };

		sliceCapab.setCubes(Arrays.asList(new SliceCube(ranges)));

		// unset slices 0-3 and 10-12
		Range[] firstRange = { new Range(0, 3) };
		Range[] secondRange = { new Range(10, 12) };

		sliceCapab.unset(new SliceCube(firstRange), new SliceCube(secondRange));

		Collection<SliceCube> cubes = sliceCapab.getAvailableCubes();

		Assert.assertEquals("There should be 2 different available slice cubes.", 2, cubes.size());

		List<SliceCube> cubeList = new ArrayList<SliceCube>(cubes);

		SliceCube firstCube = cubeList.get(0);
		SliceCube secondCube = cubeList.get(1);

		Assert.assertNotNull(firstCube);
		Assert.assertNotNull(secondCube);

		Assert.assertTrue(firstCube.getRanges().length == 1);
		Assert.assertTrue(secondCube.getRanges().length == 1);

		Assert.assertTrue("First cube should contain as lower bound port 4.", firstCube.getRanges()[0].getLowerBound() == 4);
		Assert.assertTrue("First cube should contain as upper bound port 9.", firstCube.getRanges()[0].getUpperBound() == 9);

		Assert.assertTrue("First cube should contain as lower bound port 13.", secondCube.getRanges()[0].getLowerBound() == 13);
		Assert.assertTrue("First cube should contain as upper bound port 15.", secondCube.getRanges()[0].getUpperBound() == 15);
	}

	@Test
	@Ignore
	public void addSliceEfficiencyTest() throws SlicingException {

		// test initialization
		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 16);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 16);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4096);

		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 16);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 16);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4096);

		// original slize contains "false" for all fields. initialize another slice with all values to true
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));

		sliceCapab.unset(new SliceCube(originalRanges));

		long startTime = System.currentTimeMillis();
		sliceCapab.add(otherSlice);
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## addSliceEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}

	@Test
	@Ignore
	public void containsEfficiencyTest() throws SlicingException {

		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 16);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 16);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4096);

		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 16);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 16);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4096);

		// initialize both slices to true
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));

		long startTime = System.currentTimeMillis();
		sliceCapab.contains(otherSlice);
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## containsEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}

	@Test
	@Ignore
	public void setEfficiencyTest() throws SlicingException {

		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 16);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 16);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4096);

		// initialize both slices to true

		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		long startTime = System.currentTimeMillis();
		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## setEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}

	@Test
	@Ignore
	public void cutSliceEfficiencyTest() throws SlicingException {

		sliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 16);
		sliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 16);
		sliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4096);

		otherSliceCapab.addUnit(PORT_SLICE_UNIT_NAME, 16);
		otherSliceCapab.addUnit(TIME_SLICE_UNIT_NAME, 16);
		otherSliceCapab.addUnit(LAMBDA_SLICE_UNIT_NAME, 4096);

		// initialize both slices to true

		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		sliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));
		otherSliceCapab.setCubes(Arrays.asList(new SliceCube(originalRanges)));

		long startTime = System.currentTimeMillis();
		sliceCapab.cut(otherSlice);
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## cutSliceEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}
}