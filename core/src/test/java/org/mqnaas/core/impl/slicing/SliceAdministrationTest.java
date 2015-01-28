package org.mqnaas.core.impl.slicing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.api.slicing.Cube;
import org.mqnaas.core.api.slicing.ISliceAdministration;
import org.mqnaas.core.api.slicing.IUnitAdministration;
import org.mqnaas.core.api.slicing.IUnitManagement;
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.api.slicing.SlicingException;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;

import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 *
 */
public class SliceAdministrationTest {

	private final static String	PORT_UNIT	= "port";
	private final static String	TIME_UNIT	= "time";
	private final static String	LAMBDA_UNIT	= "lambda";

	Slice						slice, otherSlice;

	private ServiceProvider		serviceProvider;

	private static void init1DSlice(Slice slice, String u, int n) {
		Unit unit = slice.addUnit(u);
		unit.setRange(new Range(0, n - 1));
	}

	private static void init2DSlice(Slice slice, String u1, int n1, String u2, int n2) {
		Unit unit1 = slice.addUnit(u1);
		unit1.setRange(new Range(0, n1 - 1));

		Unit unit2 = slice.addUnit(u2);
		unit2.setRange(new Range(0, n2 - 1));
	}

	private static void init3DSlice(Slice slice, String u1, int n1, String u2, int n2, String u3, int n3) {
		Unit unit1 = slice.addUnit(u1);
		unit1.setRange(new Range(0, n1 - 1));

		Unit unit2 = slice.addUnit(u2);
		unit2.setRange(new Range(0, n2 - 1));

		Unit unit3 = slice.addUnit(u3);
		unit3.setRange(new Range(0, n3 - 1));
	}

	@Before
	public void prepareTest() throws Exception {

		serviceProvider = new ServiceProvider();

		slice = new Slice(new SliceResource(), serviceProvider);
		otherSlice = new Slice(new SliceResource(), serviceProvider);
	}

	private class ServiceProvider implements IServiceProvider {

		private Map<IResource, Map<Class<?>, ICapability>>	resource2capabilities;

		public ServiceProvider() {
			resource2capabilities = new HashMap<IResource, Map<Class<?>, ICapability>>();
		}

		@Override
		public void activate() {
		}

		@Override
		public void deactivate() {
		}

		@Override
		public Multimap<Class<? extends IApplication>, IService> getServices(IResource resource) {
			throw new RuntimeException("Not implemented.");
		}

		@Override
		public IService getService(IResource resource, String serviceName, Class<?>... parameters) throws ServiceNotFoundException {
			throw new RuntimeException("Not implemented.");
		}

		@Override
		public IService getApplicationService(IApplication application, String serviceName, Class<?>... parameters) throws ServiceNotFoundException {
			throw new RuntimeException("Not implemented.");
		}

		@Override
		public <C extends ICapability> C getCapability(IResource resource, Class<C> capabilityClass) throws CapabilityNotFoundException {

			Map<Class<?>, ICapability> capabilities;
			if (!resource2capabilities.containsKey(resource)) {
				capabilities = new HashMap<Class<?>, ICapability>();
				resource2capabilities.put(resource, capabilities);
			} else {
				capabilities = resource2capabilities.get(resource);
			}

			if (!capabilities.containsKey(capabilityClass)) {

				ICapability capability;
				if (capabilityClass.equals(ISliceAdministration.class)) {
					capability = new SliceAdministration();

					try {
						ReflectionTestHelper.injectPrivateField(capability, serviceProvider, "serviceProvider");
						ReflectionTestHelper.injectPrivateField(capability, resource, "resource");
					} catch (Exception e) {
						new RuntimeException("ServiceProvider injection failed", e);
					}

				} else if (capabilityClass.equals(IUnitManagement.class)) {
					capability = new UnitManagment();
				} else if (capabilityClass.equals(IUnitAdministration.class)) {
					capability = new UnitAdministration();
				} else {
					throw new RuntimeException("Unknown capability class:" + capabilityClass);
				}

				try {
					ReflectionTestHelper.injectPrivateField(capability, resource, "resource");
					capability.activate();
				} catch (Exception a) {
					throw new RuntimeException("Could not activate capability " + capability.getClass().getName(), a);

				}
				capabilities.put(capabilityClass, capability);
			}

			return (C) capabilities.get(capabilityClass);

			// System.out.println("Asking for capability " + capabilityClass.getName() + " of resource " + resource);

			// sliceCapab = PowerMockito.spy(new SliceAdministration());
			// otherSliceCapab = new SliceAdministration();
			//
			// sliceCapab.activate();
			// otherSliceCapab.activate();

			// Mockito.when(serviceProvider.getCapability(Mockito.any(IResource.class), Mockito.any(Class.class))).thenReturn(otherSliceCapab);
			//
			// ReflectionTestHelper.injectPrivateField(sliceCapab, serviceProvider, "serviceProvider");

			// throw new RuntimeException("Not implemented.");
		}

		@Override
		public void printAvailableServices() {
			throw new RuntimeException("Not implemented.");
		}

		@Override
		public <C extends ICapability> C getCapabilityInstance(IResource resource, Class<C> capabilityClass)
				throws CapabilityNotFoundException {
			throw new RuntimeException("Not implemented.");
		}

	}

	@Test
	public void setSliceCube2DTest() {

		// initialize sliceAdministrationCapability, define port-time slice with 2x4 dimensions.
		init2DSlice(slice, PORT_UNIT, 2, TIME_UNIT, 4);

		// initialize cube : interfaces (0-1) and vlans (1-3)
		Range[] ranges = new Range[2];
		ranges[0] = new Range(0, 1);
		ranges[1] = new Range(1, 3);
		Cube cube = new Cube(ranges);

		// test and asserts
		slice.setCubes(Arrays.asList(cube));

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

		Assert.assertEquals("Slice representation should match the one stored in string builder.", sb.toString(), slice.toMatrix());

		System.out.println("######################");
		System.out.println("## setSliceCubeTest ##");
		System.out.println("######################\n");
		System.out.println("Final slice:");
		System.out.println(slice.toMatrix());
	}

	/**
	 * Test checks that {@link OldSlice#contains(OldSlice)} method fails if both slices have different number of dimensions (in this case (1D vs 2D)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void containsSliceDifferentDimensionsTest() throws Exception {

		// initialize sliceAdministrationCapability, define port slice with size 2
		init1DSlice(slice, PORT_UNIT, 2);

		// initialize another sliceAdministrationCapability, define port-time slice with 2x3 slice
		init2DSlice(otherSlice, PORT_UNIT, 2, TIME_UNIT, 3);

		slice.contains(otherSlice);
	}

	//
	/**
	 * Test checks that {@link OldSlice#contains(OldSlice)} method fails if slice units are not defined in same order in both slices.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void containsSliceDifferentSliceOrder() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time slice
		init2DSlice(slice, PORT_UNIT, 2, TIME_UNIT, 3);

		// initialize sliceAdministrationCapability, define inverse slice: time-port
		init2DSlice(otherSlice, TIME_UNIT, 3, PORT_UNIT, 2);

		slice.contains(otherSlice);
	}

	@Test
	@Ignore
	public void constains3DTest() throws SlicingException {

		// #########################
		// ## TEST INITIALIZATION ##
		// #########################

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		Unit unitPort = slice.addUnit(PORT_UNIT);
		unitPort.setRange(new Range(0, 1));

		Unit unitTime = slice.addUnit(TIME_UNIT);
		unitTime.setRange(new Range(0, 2));

		Unit unitLambda = slice.addUnit(LAMBDA_UNIT);
		unitLambda.setRange(new Range(0, 3));

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		unitPort = otherSlice.addUnit(PORT_UNIT);
		unitPort.setRange(new Range(0, 1));

		unitTime = otherSlice.addUnit(TIME_UNIT);
		unitTime.setRange(new Range(0, 2));

		unitLambda = otherSlice.addUnit(LAMBDA_UNIT);
		unitLambda.setRange(new Range(0, 3));

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

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		Assert.assertTrue("OtherSice should be contained in original slice.", slice.contains(otherSlice));

		// ##########################
		// ## TEST EXPECTING FALSE ##
		// ##########################

		otherSlice.initData();

		// initialize sub-cube, ports(0-1), time(0-2), slice(0-3). Should not be contained in original one, since time[2] is not in originaLSlice.
		otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 2);
		otherRanges[2] = new Range(0, 3);

		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		Assert.assertFalse("OtherSice should not be contained in original slice.", slice.contains(otherSlice));

		// ###################################
		// ## TEST WITH SET OF CUBES - TRUE ##
		// ###################################

		otherSlice.initData();

		// initialize sub-cubes: port(0), time(0), slice(0) and port(1), time(1), slice(2-3). Should be contained in original one.
		otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 0);
		otherRanges[1] = new Range(0, 0);
		otherRanges[2] = new Range(0, 0);

		Range[] anotherRanges = new Range[3];
		anotherRanges[0] = new Range(1, 1);
		anotherRanges[1] = new Range(1, 1);
		anotherRanges[2] = new Range(2, 3);

		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges), new Cube(anotherRanges)));

		Assert.assertTrue("OtherSice should be contained in original slice.", slice.contains(otherSlice));

		// ####################################
		// ## TEST WITH SET OF CUBES - FALSE ##
		// ####################################

		// initialize sub-cubes, port(0), time(0), slice(0) and port(1), time(1-2), slice(2-3). Should not be contained in original one, since time[2]
		// is not in originaLSlice.

		otherSlice.initData();

		otherRanges = new Range[3];
		otherRanges[0] = new Range(0, 0);
		otherRanges[1] = new Range(0, 0);
		otherRanges[2] = new Range(0, 0);

		anotherRanges = new Range[3];
		anotherRanges[0] = new Range(1, 1);
		anotherRanges[1] = new Range(1, 2);
		anotherRanges[2] = new Range(2, 3);

		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges), new Cube(anotherRanges)));

		Assert.assertFalse("OtherSice should not be contained in original slice.", slice.contains(otherSlice));
	}

	@Test
	public void contains1DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port slice of size 4
		init1DSlice(slice, PORT_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port slice of size 4
		init1DSlice(otherSlice, PORT_UNIT, 4);

		// #########################
		// ## TEST EXPECTING TRUE ##
		// #########################

		// initialize original cube : ports (0-2)
		Range[] originalRanges = new Range[1];
		originalRanges[0] = new Range(0, 2);

		// initialize sub-cube, ports(0-1). Should be contained in original one.
		Range[] otherRanges = new Range[1];
		otherRanges[0] = new Range(0, 1);

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		Assert.assertTrue("OtherSice should be contained in original slice.", slice.contains(otherSlice));

		// ##########################
		// ## TEST EXPECTING FALSE ##
		// ##########################

		otherSlice.initData();

		// initialize sub-cube, ports(0-3)
		otherRanges = new Range[1];
		otherRanges[0] = new Range(0, 3);

		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		Assert.assertFalse("OtherSice should not be contained in original slice.", slice.contains(otherSlice));

	}

	@Test
	public void contains2DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time 3x4 slice
		init2DSlice(slice, PORT_UNIT, 3, TIME_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port-time slice of size 3x4
		init2DSlice(otherSlice, PORT_UNIT, 3, TIME_UNIT, 4);

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

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		Assert.assertTrue("OtherSice should be contained in original slice.", slice.contains(otherSlice));

		// ##########################
		// ## TEST EXPECTING FALSE ##
		// ##########################

		otherSlice.initData();

		// initialize sub-cube, ports(0-1), time(0-2) Should not be contained in original one, since time[2] is not in originaLSlice.
		otherRanges = new Range[2];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 2);

		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		Assert.assertFalse("OtherSice should not be contained in original slice.", slice.contains(otherSlice));

	}

	@Test(expected = SlicingException.class)
	public void addSliceAlreadyExistingValues3DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(slice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		Unit unitPort = otherSlice.addUnit(PORT_UNIT);
		unitPort.setRange(new Range(0, 1));
		Unit unitTime = otherSlice.addUnit(TIME_UNIT);
		unitTime.setRange(new Range(0, 2));
		Unit unitLambda = otherSlice.addUnit(LAMBDA_UNIT);
		unitLambda.setRange(new Range(0, 3));

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

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		// call to method -> launch exception
		slice.add(otherSlice);

	}

	@Test
	public void addSlice3DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		Unit unitPort = slice.addUnit(PORT_UNIT);
		unitPort.setRange(new Range(0, 1));
		Unit unitTime = slice.addUnit(TIME_UNIT);
		unitTime.setRange(new Range(0, 2));
		Unit unitLambda = slice.addUnit(LAMBDA_UNIT);
		unitLambda.setRange(new Range(0, 3));

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		unitPort = otherSlice.addUnit(PORT_UNIT);
		unitPort.setRange(new Range(0, 1));
		unitTime = otherSlice.addUnit(TIME_UNIT);
		unitTime.setRange(new Range(0, 2));
		unitLambda = otherSlice.addUnit(LAMBDA_UNIT);
		unitLambda.setRange(new Range(0, 3));

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

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		// we remove ports (0-1), time (2-2), lambda(0-3) from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(0, 1), new Range(2, 2), new Range(0, 3) };
		Cube cube = new Cube(ranges);
		slice.unset(cube);

		System.out.println("Original Slice:");
		System.out.println(slice.toMatrix());
		System.out.println("Slice to add :");
		System.out.println(otherSlice.toMatrix());

		// call to method
		slice.add(otherSlice);

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

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), slice.toMatrix());

		System.out.println("Final slice.");
		System.out.println(slice.toMatrix());

	}

	@Test
	public void addSlice2DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time 3x4 slice
		Unit unitPort = slice.addUnit(PORT_UNIT);
		unitPort.setRange(new Range(0, 2));

		Unit unitTime = slice.addUnit(TIME_UNIT);
		unitTime.setRange(new Range(0, 3));

		// initialize another sliceAdministrationCapability, define port-time 3x4 slice
		unitPort = otherSlice.addUnit(PORT_UNIT);
		unitPort.setRange(new Range(0, 2));
		unitTime = otherSlice.addUnit(TIME_UNIT);
		unitTime.setRange(new Range(0, 3));

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

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		// we remove ports (0-1), time (2-3) from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(0, 1), new Range(2, 3) };
		Cube cube = new Cube(ranges);
		slice.unset(cube);

		System.out.println("Original Slice:");
		System.out.println(slice.toMatrix());
		System.out.println("Slice to add :");
		System.out.println(otherSlice.toMatrix());

		// call to method
		slice.add(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XXXX").append("\n");
		sb.append("XXXX").append("\n");
		sb.append("OOOO").append("\n");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), slice.toMatrix());

		System.out.println("Final slice.");
		System.out.println(slice.toMatrix());

	}

	@Test
	public void addSlice1DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port slice of size 4
		init1DSlice(slice, PORT_UNIT, 4);
		// initialize another sliceAdministrationCapability, define port slice of size 4
		init1DSlice(otherSlice, PORT_UNIT, 4);

		// initialize original cube : ports (0-3)
		Range[] originalRanges = new Range[] { new Range(0, 3) };

		// initialize sub-cube, ports(3)
		Range[] otherRanges = new Range[] { new Range(3, 3) };

		System.out.println("####################");
		System.out.println("## addSlice1DTest ##");
		System.out.println("####################\n");

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		System.out.println("Current Slice:");
		System.out.println(slice.toMatrix());
		System.out.println("Slice to add :");
		System.out.println(otherSlice.toMatrix());

		// we remove element[3] from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(3, 3) };
		Cube cube = new Cube(ranges);
		slice.unset(cube);

		// call add method
		slice.add(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XXXX");

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), slice.toMatrix());

		System.out.println("Final slice.");
		System.out.println(slice.toMatrix());
	}

	@Test
	public void addSliceNotContinousCubes3DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(slice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(otherSlice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

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

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges), new Cube(secondOtherRanges)));

		// we remove ports (0-1), time (0-1), lambda(1,3) from slice (it will remove it from currentData, not originalData!)
		Range[] ranges = { new Range(0, 1), new Range(0, 1), new Range(1, 1) };
		slice.unset(new Cube(ranges));
		Range[] secondRanges = { new Range(0, 1), new Range(0, 1), new Range(3, 3) };
		slice.unset(new Cube(secondRanges));

		System.out.println("#####################################");
		System.out.println("## addSliceNotContinousCubes3DTest ##");
		System.out.println("#####################################\n");

		System.out.println("Original Slice:");
		System.out.println(slice.toMatrix());
		System.out.println("Slice to add :");
		System.out.println(otherSlice.toMatrix());
		// call to method
		slice.add(otherSlice);

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

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), slice.toMatrix());

		System.out.println("Final slice.");
		System.out.println(slice.toMatrix());

	}

	@Test
	public void cutSlice3Dtest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(slice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(otherSlice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

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

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		System.out.println("####################");
		System.out.println("## cutSlice3Dtest ##");
		System.out.println("####################\n");

		System.out.println("Original Slice:");
		System.out.println(slice.toMatrix());
		System.out.println("Slice to cut :");
		System.out.println(otherSlice.toMatrix());

		// call to method
		slice.cut(otherSlice);

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

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), slice.toMatrix());

		System.out.println("Final slice.");
		System.out.println(slice.toMatrix());

	}

	@Test
	public void cutSlice2DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time 3x4 slice
		init2DSlice(slice, PORT_UNIT, 3, TIME_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port-time 3x4 slice
		init2DSlice(otherSlice, PORT_UNIT, 3, TIME_UNIT, 4);

		// initialize original slice with cube : ports (0-1), time (0-1)
		Range[] originalRanges = new Range[2];
		originalRanges[0] = new Range(0, 1);
		originalRanges[1] = new Range(0, 1);

		// initialize another slice with: cube : ports (0-1), time (0)
		Range[] otherRanges = new Range[2];
		otherRanges[0] = new Range(0, 1);
		otherRanges[1] = new Range(0, 0);

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		System.out.println("####################");
		System.out.println("## cutSlice2DTest ##");
		System.out.println("####################\n");

		System.out.println("Original Slice:");
		System.out.println(slice.toMatrix());
		System.out.println("Slice to cut :");
		System.out.println(otherSlice.toMatrix());

		// call to method
		slice.cut(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("OXOO").append("\n");
		sb.append("OXOO").append("\n");
		sb.append("OOOO").append("\n");
		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), slice.toMatrix());

		System.out.println("Final slice.");
		System.out.println(slice.toMatrix());

	}

	@Test
	public void cutSlice1DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port slice of size 4
		init1DSlice(slice, PORT_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port slice of size 4
		init1DSlice(otherSlice, PORT_UNIT, 4);

		// initialize original slice with cube : ports (0-2)
		Range[] originalRanges = new Range[1];
		originalRanges[0] = new Range(0, 2);

		// initialize another slice with: cube : ports (1-2)
		Range[] otherRanges = new Range[1];
		otherRanges[0] = new Range(1, 2);

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges)));

		System.out.println("####################");
		System.out.println("## cutSlice1DTest ##");
		System.out.println("####################\n");

		System.out.println("Original Slice:");
		System.out.println(slice.toMatrix());
		System.out.println("Slice to cut :");
		System.out.println(otherSlice.toMatrix());

		// call to method
		slice.cut(otherSlice);

		// asserts and print final slice
		StringBuilder sb = new StringBuilder();
		sb.append("XOOO");
		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), slice.toMatrix());

		System.out.println("Final slice.");
		System.out.println(slice.toMatrix());

	}

	@Test
	public void cutSliceNotContinousCubes3DTest() throws SlicingException {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(slice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(otherSlice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

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

		slice.setCubes(Arrays.asList(new Cube(originalRanges), new Cube(secondOriginalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(otherRanges), new Cube(secondOtherRanges)));

		System.out.println("#####################################");
		System.out.println("## cutSliceNotContinousCubes3DTest ##");
		System.out.println("#####################################\n");

		System.out.println("Original Slice:");
		System.out.println(slice.toMatrix());
		System.out.println("Slice to cut :");
		System.out.println(otherSlice.toMatrix());

		// call to method
		slice.cut(otherSlice);

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

		Assert.assertEquals("Final slice does not look like it meant to. ", sb.toString(), slice.toMatrix());

		System.out.println("Final slice.");
		System.out.println(slice.toMatrix());

	}

	@Test
	public void isInOperationalStateTest() {

		// initialize sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(slice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

		// initialize original slice with cube : ports (0-1), time (0-1), slice(0,0)
		Range[] ranges = new Range[3];
		ranges[0] = new Range(0, 1);
		ranges[1] = new Range(0, 1);
		ranges[2] = new Range(0, 0);

		slice.setCubes(Arrays.asList(new Cube(ranges)));

		Assert.assertFalse("Slice should not be in operational state, since it contains same current and original data.",
				slice.isInOperationalState());

		ranges = new Range[3];
		ranges[0] = new Range(0, 0);
		ranges[1] = new Range(0, 1);
		ranges[2] = new Range(0, 0);
		slice.unset(Arrays.asList(new Cube[] { new Cube(ranges) }));

		Assert.assertTrue("Slice should  be in operational state, since it does not contain same current and original data.",
				slice.isInOperationalState());

	}

	@Test
	public void getCubes1DTest() {

		// initialize sliceAdministrationCapability, define port slice of 16 size
		init1DSlice(slice, PORT_UNIT, 16);

		// initialize slices 0-3 and 10-12
		Range[] ranges = { new Range(0, 3) };
		Range[] secondRanges = { new Range(10, 12) };

		slice.setCubes(Arrays.asList(new Cube(ranges), new Cube(secondRanges)));

		Collection<Cube> cubes = slice.getCubes();

		Assert.assertEquals("There should be 2 different slice cubes.", 2, cubes.size());

		List<Cube> cubeList = new ArrayList<Cube>(cubes);

		Cube firstCube = cubeList.get(0);
		Cube secondCube = cubeList.get(1);

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
		init3DSlice(slice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(otherSlice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

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

		slice.setCubes(Arrays.asList(new Cube(originalRanges), new Cube(otherRanges)));

		Collection<Cube> cubes = slice.getCubes();

		Assert.assertEquals("There should be 2 different slice cubes.", 2, cubes.size());

		List<Cube> cubeList = new ArrayList<Cube>(cubes);

		Cube firstCube = cubeList.get(0);
		Cube secondCube = cubeList.get(1);

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
		init3DSlice(slice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

		// initialize another sliceAdministrationCapability, define port-time-lambda 2x3x4 slice
		init3DSlice(otherSlice, PORT_UNIT, 2, TIME_UNIT, 3, LAMBDA_UNIT, 4);

		// initialize slice with all cubes available
		Range[] wholeRange = new Range[3];
		wholeRange[0] = new Range(0, 1);
		wholeRange[1] = new Range(0, 2);
		wholeRange[2] = new Range(0, 3);

		slice.setCubes(Arrays.asList(new Cube(wholeRange)));

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

		slice.unset(Arrays.asList(new Cube[] { new Cube(originalRanges), new Cube(otherRanges) }));

		Collection<Cube> cubes = slice.getAvailableCubes();

		Assert.assertEquals("There should be 2 different slice cubes.", 2, cubes.size());

		List<Cube> cubeList = new ArrayList<Cube>(cubes);

		Cube firstCube = cubeList.get(0);
		Cube secondCube = cubeList.get(1);

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
		init1DSlice(slice, PORT_UNIT, 16);

		// initialize whole slice.
		Range[] ranges = { new Range(0, 15) };

		slice.setCubes(Arrays.asList(new Cube(ranges)));

		// unset slices 0-3 and 10-12
		Range[] firstRange = { new Range(0, 3) };
		Range[] secondRange = { new Range(10, 12) };

		slice.unset(Arrays.asList(new Cube[] { new Cube(firstRange), new Cube(secondRange) }));

		Collection<Cube> cubes = slice.getAvailableCubes();

		Assert.assertEquals("There should be 2 different available slice cubes.", 2, cubes.size());

		List<Cube> cubeList = new ArrayList<Cube>(cubes);

		Cube firstCube = cubeList.get(0);
		Cube secondCube = cubeList.get(1);

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
		init3DSlice(slice, PORT_UNIT, 16, TIME_UNIT, 16, LAMBDA_UNIT, 4096);
		init3DSlice(otherSlice, PORT_UNIT, 16, TIME_UNIT, 16, LAMBDA_UNIT, 4096);

		// original slize contains "false" for all fields. initialize another slice with all values to true
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(originalRanges)));

		slice.unset(Arrays.asList(new Cube[] { new Cube(originalRanges) }));

		long startTime = System.currentTimeMillis();
		slice.add(otherSlice);
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## addSliceEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}

	@Test
	@Ignore
	public void containsEfficiencyTest() throws SlicingException {

		init3DSlice(slice, PORT_UNIT, 16, TIME_UNIT, 16, LAMBDA_UNIT, 4096);
		init3DSlice(otherSlice, PORT_UNIT, 16, TIME_UNIT, 16, LAMBDA_UNIT, 4096);

		// initialize both slices to true
		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(originalRanges)));

		long startTime = System.currentTimeMillis();
		slice.contains(otherSlice);
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## containsEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}

	@Test
	@Ignore
	public void setEfficiencyTest() throws SlicingException {

		init3DSlice(slice, PORT_UNIT, 16, TIME_UNIT, 16, LAMBDA_UNIT, 4096);

		// initialize both slices to true

		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		long startTime = System.currentTimeMillis();
		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## setEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}

	@Test
	@Ignore
	public void cutSliceEfficiencyTest() throws SlicingException {

		init3DSlice(slice, PORT_UNIT, 16, TIME_UNIT, 16, LAMBDA_UNIT, 4096);
		init3DSlice(otherSlice, PORT_UNIT, 16, TIME_UNIT, 16, LAMBDA_UNIT, 4096);

		// initialize both slices to true

		Range[] originalRanges = new Range[3];
		originalRanges[0] = new Range(0, 15);
		originalRanges[1] = new Range(0, 15);
		originalRanges[2] = new Range(0, 4095);

		slice.setCubes(Arrays.asList(new Cube(originalRanges)));
		otherSlice.setCubes(Arrays.asList(new Cube(originalRanges)));

		long startTime = System.currentTimeMillis();
		slice.cut(otherSlice);
		long endTime = System.currentTimeMillis();

		System.out.println("#####################################");
		System.out.println("## cutSliceEfficiencyTest ##");
		System.out.println("#####################################\n");
		System.out.println("Execution time: " + (endTime - startTime) + "ms");

	}
}