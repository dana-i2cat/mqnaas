package org.mqnaas.core.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mqnaas.core.api.IBindingDecider;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IObservationService;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.impl.dummy.DummyBundleGuard;
import org.mqnaas.core.impl.notificationfilter.ServiceFilter;

public class BindingManagementReactsToResourcesTest {

	static IRootResourceManagement	resourceManagement;
	static BindingManagement		bindingManagement;
	static IObservationService		observationService;
	static ISampleMgmtCapability	sampleMgmtCapability;
	static IExecutionService		executionService;

	boolean							addExecuted		= false;
	boolean							removeExecuted	= false;

	@BeforeClass
	public static void init() throws Exception {

		resourceManagement = new RootResourceManagement();

		IBindingDecider bindingDecider = new IBindingDecider() {
			@Override
			public boolean shouldBeBound(IResource resource, Class<? extends ICapability> capabilityClass) {
				return true;
			}
		};

		ExecutionService executionServiceInstance = new ExecutionService();

		bindingManagement = new BindingManagement();
		bindingManagement.setResourceManagement(resourceManagement);
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
	public void addResourceTriggersResourceAddedExecution() throws ServiceNotFoundException, SecurityException, NoSuchMethodException {

		// register service that notifies test when observed resourceAdded is executed
		IService observedAdd = bindingManagement.getService(
				bindingManagement.getResourceCapabilityTree().getRootResourceNode().getContent(),
				"resourceAdded", IResource.class, ICapability.class);

		Method addExecutedM = MyTest.class.getMethod("addExecuted", new Class[0]);
		observationService.registerObservation(new ServiceFilter(observedAdd),
				new Service(null, new ServiceMetaData(addExecutedM, null)) {

					@Override
					public Object execute(Object[] parameters) {
						addExecuted = true;
						return null;
					}
				});

		// register service that notifies test when observed resourceRemoved is executed
		IService observedRemove = bindingManagement.getService(
				bindingManagement.getResourceCapabilityTree().getRootResourceNode().getContent(),
				"resourceRemoved", IResource.class, ICapability.class);

		Method removeExecutedM = MyTest.class.getMethod("removeExecuted", new Class[0]);
		observationService.registerObservation(new ServiceFilter(observedRemove),
				new Service(null, new ServiceMetaData(removeExecutedM, null)) {

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
		public void addExecuted() {
			addExecuted = true;
		}

		public void removeExecuted() {
			removeExecuted = true;
		}
	}
}
