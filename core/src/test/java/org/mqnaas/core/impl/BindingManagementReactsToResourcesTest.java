package org.mqnaas.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IBindingDecider;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IObservationService;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.impl.dummy.DummyBundleGuard;
import org.mqnaas.core.impl.notificationfilter.ServiceFilter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

//needed to mock static method of FrameworkUtil class.
@RunWith(PowerMockRunner.class)
@PrepareForTest(FrameworkUtil.class)
public class BindingManagementReactsToResourcesTest {

	static RootResourceManagement	resourceManagement;
	static BindingManagement		bindingManagement;
	static IObservationService		observationService;
	static ISampleMgmtCapability	sampleMgmtCapability;
	static IExecutionService		executionService;

	volatile boolean				addExecuted		= false;
	volatile boolean				removeExecuted	= false;

	@BeforeClass
	public static void init() throws Exception {

		resourceManagement = new RootResourceManagement();

		IBindingDecider bindingDecider = new IBindingDecider() {
			@Override
			public boolean shouldBeBound(IResource resource, Class<? extends ICapability> capabilityClass) {
				return true;
			}

			@Override
			public void activate() {
			}

			@Override
			public void deactivate() {
			}
		};

		// mock OSGI related code
		BundleContext mockedContext = PowerMockito.mock(BundleContext.class);
		Bundle mockedBundle = PowerMockito.mock(Bundle.class);

		PowerMockito.when(mockedContext.registerService(Class.class, Class.class, null)).thenReturn(null);
		PowerMockito.when(mockedBundle.getBundleContext()).thenReturn(mockedContext);
		PowerMockito.mockStatic(FrameworkUtil.class);
		PowerMockito.when(FrameworkUtil.getBundle(BindingManagement.class)).thenReturn(mockedBundle);

		ExecutionService executionServiceInstance = new ExecutionService();

		bindingManagement = new BindingManagement();
		bindingManagement.setResourceAdministration(resourceManagement);
		bindingManagement.setResourceProvider(resourceManagement);
		bindingManagement.setBindingDecider(bindingDecider);
		bindingManagement.setExecutionService(executionServiceInstance);
		bindingManagement.setObservationService(executionServiceInstance);
		bindingManagement.setBundleGuard(new DummyBundleGuard());

		bindingManagement.init();

		List<Class<? extends ICapability>> knownCapabilities = new ArrayList<Class<? extends ICapability>>();
		knownCapabilities.add(BindingManagement.class);
		knownCapabilities.add(RootResourceManagement.class);
		knownCapabilities.add(bindingDecider.getClass());
		knownCapabilities.add(ExecutionService.class);
		knownCapabilities.add(SampleMgmtCapability.class);
		bindingManagement.capabilitiesAdded(knownCapabilities);

		observationService = executionServiceInstance;
		executionService = executionServiceInstance;

	}

	@Test
	public void addResourceTriggersResourceAddedExecution() throws ServiceNotFoundException, SecurityException, NoSuchMethodException,
			InvocationTargetException {

		// register service that notifies test when observed resourceAdded is executed
		Class<?>[] serviceParameters = new Class<?>[3];
		serviceParameters[0] = IResource.class;
		serviceParameters[1] = IApplication.class;
		serviceParameters[2] = Class.class;
		
		IService observedAdd = bindingManagement.getService(
				bindingManagement.getResourceCapabilityTree().getRootResourceNode().getContent(),
				"resourceAdded", serviceParameters);

		Method addExecutedM = MyTest.class.getMethod("addExecuted", new Class[0]);
		observationService.registerObservation(new ServiceFilter(observedAdd),
				new Service(addExecutedM, null, null) {

					@Override
					public Object execute(Object[] parameters) {
						addExecuted = true;
						return null;
					}
				});

		// register service that notifies test when observed resourceRemoved is executed
		IService observedRemove = bindingManagement.getService(
				bindingManagement.getResourceCapabilityTree().getRootResourceNode().getContent(),
				"resourceRemoved", serviceParameters);

		Method removeExecutedM = MyTest.class.getMethod("removeExecuted", new Class[0]);
		observationService.registerObservation(new ServiceFilter(observedRemove),
				new Service(removeExecutedM, null, null) {

					@Override
					public Object execute(Object[] parameters) {
						removeExecuted = true;
						return null;
					}
				});

		Object[] parameters = new Object[1];
		parameters[0] = new SampleResource();

		addExecuted = false;
		removeExecuted = false;

		executionService.execute(bindingManagement.getService(
				bindingManagement.getResourceCapabilityTree().getRootResourceNode().getContent(),
				"addSampleResource", SampleResource.class),
				parameters);

		Assert.assertTrue("resourceAdded should be executed", addExecuted);

		executionService.execute(bindingManagement.getService(
				bindingManagement.getResourceCapabilityTree().getRootResourceNode().getContent(),
				"removeSampleResource", SampleResource.class),
				parameters);

		Assert.assertTrue("resourceRemoved should be executed", removeExecuted);
	}

	private class MyTest {
		// Although the method is not called in the tests, it is used to create a service notifying the test when a resource is added
		@SuppressWarnings("unused")
		public void addExecuted() {
			addExecuted = true;
		}

		// Although the method is not called in the tests, it is used to create a service notifying the test when a resource is removed
		@SuppressWarnings("unused")
		public void removeExecuted() {
			removeExecuted = true;
		}
	}
}
