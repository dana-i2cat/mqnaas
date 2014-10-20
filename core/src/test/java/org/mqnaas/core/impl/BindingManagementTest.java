package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IBindingDecider;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.impl.dummy.DummyBundleGuard;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceCapabilityTreeController;
import org.mqnaas.core.impl.resourcetree.ResourceNode;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class BindingManagementTest {

	static IRootResourceManagement	resourceManagement;
	static BindingManagement		bindingManagement;

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

		ExecutionService executionServiceInstance = new ExecutionService();

		bindingManagement = new BindingManagement();
		bindingManagement.setResourceManagement(resourceManagement);
		bindingManagement.setBindingDecider(bindingDecider);
		bindingManagement.setExecutionService(executionServiceInstance);
		bindingManagement.setObservationService(executionServiceInstance);
		// use dummy BundleGuard
		bindingManagement.setBundleGuard(new DummyBundleGuard());

		bindingManagement.init();

		List<Class<? extends ICapability>> knownCapabilities = new ArrayList<Class<? extends ICapability>>();
		knownCapabilities.add(BindingManagement.class);
		knownCapabilities.add(RootResourceManagement.class);
		knownCapabilities.add(bindingDecider.getClass());
		knownCapabilities.add(ExecutionService.class);

		bindingManagement.capabilitiesAdded(knownCapabilities);

		List<Class<? extends IApplication>> knownApplications = new ArrayList<Class<? extends IApplication>>();
		knownApplications.add(SampleApplication.class);
		bindingManagement.applicationsAdded(knownApplications);
	}

	@Test
	public void bindCapabilityInstanceToResource() throws ResourceNotFoundException {

		IResource resource = resourceManagement.getCore();

		CapabilityInstance ci = new CapabilityInstance(SampleCapability.class);
		bindingManagement.bind(new CapabilityNode(ci), bindingManagement.getResourceCapabilityTree().getRootResourceNode());

		Assert.assertTrue("CI should be bound to the resource",
				bindingManagement.getCapabilityInstancesBoundToResource(resource).contains(ci));

		Assert.assertNotNull("Services in ISampleCapability should be available for resource",
				bindingManagement.getServices(resource).get(ISampleCapability.class));
		Assert.assertFalse("Services in ISampleCapability should be available for resource",
				bindingManagement.getServices(resource).get(ISampleCapability.class).isEmpty());

		Object resourceValue = ((SampleCapability) ci.getInstance()).getResource();
		Assert.assertEquals("Resource must be injected in capability.", resource, resourceValue);
	}

	@Test
	public void unbindCapabilityInstanceToResource() throws ResourceNotFoundException {

		IResource res = resourceManagement.getCore();
		ResourceNode resource = bindingManagement.getResourceCapabilityTree().getRootResourceNode();

		CapabilityInstance ci = getCapabilityInstanceBoundToResource(res, SampleCapability.class);
		CapabilityNode capability = ResourceCapabilityTreeController.getChidrenWithContent(resource, ci);

		bindingManagement.unbind(capability, resource);

		Assert.assertFalse("CI should NOT be bound to the resource",
				bindingManagement.getCapabilityInstancesBoundToResource(res).contains(ci));

		Assert.assertTrue("Services in ISampleCapability should NOT be available for resource",
				bindingManagement.getServices(res).get(ISampleCapability.class).isEmpty());
	}

	@Test
	public void addResourceInCapabilityInstance() throws ResourceNotFoundException {

		IResource core = resourceManagement.getCore();

		CapabilityInstance sampleCI = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		Assert.assertNotNull(sampleCI);

		IResource sampleResource = generateSampleResource();

		bindingManagement.resourceAdded(sampleResource, sampleCI.getInstance());

		Assert.assertTrue("SampleResource should be provided by SampleCapability",
				bindingManagement.getResourcesProvidedByCapabilityInstance(sampleCI).contains(sampleResource));
	}

	@Test
	public void removeResourceInCapabilityInstance() throws ResourceNotFoundException {

		IResource core = resourceManagement.getCore();

		CapabilityInstance sampleCI = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		Assert.assertNotNull(sampleCI);

		ResourceNode root = bindingManagement.getResourceCapabilityTree().getRootResourceNode();
		CapabilityNode capability = ResourceCapabilityTreeController.getCapabilityNodeWithContent(root, sampleCI);

		ResourceNode sampleResourceNode = capability.getChildren().get(0);
		IResource sampleResource = sampleResourceNode.getContent();

		bindingManagement.resourceRemoved(sampleResource, sampleCI.getInstance());

		Assert.assertFalse("SampleResource should NOT provided by SampleCapability",
				bindingManagement.getResourcesProvidedByCapabilityInstance(sampleCI).contains(sampleResource));
	}

	@Test
	public void newCapabilitiesAreAutomaticallyBoundToResources() throws ResourceNotFoundException {

		// adding SampleCapability used in this test
		List<Class<? extends ICapability>> capabilitiyClasses = new ArrayList<Class<? extends ICapability>>(1);
		capabilitiyClasses.add(SampleCapability.class);
		bindingManagement.capabilitiesAdded(capabilitiyClasses);

		IResource core = resourceManagement.getCore();

		// following check relies on bindingDecider.shouldBeBound(core, SampleCapability.class) returning true
		// which is the trigger for a CapabilityInstance with SampleCapability being bound to sampleResource
		CapabilityInstance sampleCIForCoreResource = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		Assert.assertNotNull("SampleCapability should be bound to the resource", sampleCIForCoreResource);

		Assert.assertNotNull("Services in ISampleCapability should be available for the resource",
				bindingManagement.getServices(core).get(ISampleCapability.class));
		Assert.assertFalse("Services in ISampleCapability should be available for the resource",
				bindingManagement.getServices(core).get(ISampleCapability.class).isEmpty());
	}

	@Test
	public void knownCapabilitiesAreAutomaticallyBoundToNewResources() throws ResourceNotFoundException {

		// adding SampleCapability used in this test
		List<Class<? extends ICapability>> capabilitiyClasses = new ArrayList<Class<? extends ICapability>>(1);
		capabilitiyClasses.add(SampleCapability.class);
		bindingManagement.capabilitiesAdded(capabilitiyClasses);

		// add new resource to bindingManagement
		IResource core = resourceManagement.getCore();
		CapabilityInstance sampleCI = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		Assert.assertNotNull(sampleCI);
		IResource sampleResource = generateSampleResource();
		bindingManagement.resourceAdded(sampleResource, sampleCI.getInstance());

		// following check relies on bindingDecider.shouldBeBound(sampleResource, SampleCapability.class) returning true
		// which is the trigger for a CapabilityInstance with SampleCapability being bound to sampleResource
		CapabilityInstance sampleCIForSampleResource = getCapabilityInstanceBoundToResource(sampleResource, SampleCapability.class);
		Assert.assertNotNull("SampleCapability should be bound to the resource", sampleCIForSampleResource);

		Assert.assertNotNull("Services in ISampleCapability should be available for the resource",
				bindingManagement.getServices(sampleResource).get(ISampleCapability.class));
		Assert.assertFalse("Services in ISampleCapability should be available for the resource",
				bindingManagement.getServices(sampleResource).get(ISampleCapability.class).isEmpty());
	}

	@Test
	public void capabilitiesAndResourcesAreUnboundInCascadeWhenResourceIsRemoved() throws ResourceNotFoundException {

		// adding SampleCapability used in this test
		List<Class<? extends ICapability>> capabilitiyClasses = new ArrayList<Class<? extends ICapability>>(1);
		capabilitiyClasses.add(SampleCapability.class);
		bindingManagement.capabilitiesAdded(capabilitiyClasses);

		// add new resource to bindingManagement
		IResource core = resourceManagement.getCore();
		CapabilityInstance coreSampleCI = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		Assert.assertNotNull(coreSampleCI);

		// create a binding chain with 5 resources with a SampleCapability each.
		List<IResource> chainResources = new ArrayList<IResource>();
		List<CapabilityInstance> chainCapabilityInstances = new ArrayList<CapabilityInstance>();

		IResource resource;
		CapabilityInstance sampleCI = coreSampleCI;
		for (int i = 0; i < 5; i++) {
			resource = generateSampleResource();
			bindingManagement.resourceAdded(resource, sampleCI.getInstance());
			sampleCI = getCapabilityInstanceBoundToResource(resource, SampleCapability.class);
			Assert.assertNotNull(sampleCI);

			chainResources.add(resource);
			chainCapabilityInstances.add(sampleCI);
		}

		// remove first resource in the chain
		IResource toRemove = chainResources.get(0);
		bindingManagement.resourceRemoved(toRemove, coreSampleCI.getInstance());
		Assert.assertFalse(bindingManagement.getResourcesProvidedByCapabilityInstance(coreSampleCI).contains(toRemove));

		for (CapabilityInstance inChain : chainCapabilityInstances)
			Assert.assertFalse(bindingManagement.getAllCapabilityInstances().contains(inChain));

		for (IResource inChain : chainResources)
			Assert.assertFalse(bindingManagement.getAllResources().contains(inChain));

		for (int i = 0; i < 5; i++) {
			Assert.assertNull("Capability should NOT be bound to resource",
					getCapabilityInstanceBoundToResource(chainResources.get(i), SampleCapability.class));
			Assert.assertTrue("Resource should NOT be provided by capability",
					bindingManagement.getResourcesProvidedByCapabilityInstance(chainCapabilityInstances.get(i)).isEmpty());
		}
	}

	private CapabilityInstance getCapabilityInstanceBoundToResource(IResource resource, Class<? extends ICapability> clazz) {
		CapabilityInstance found = null;
		for (CapabilityInstance capabilityInstance : bindingManagement.getCapabilityInstancesBoundToResource(resource)) {
			if (clazz.equals(capabilityInstance.getClazz())) {
				found = capabilityInstance;
			}
		}
		return found;
	}

	private IResource generateSampleResource() {
		return new SampleResource();
	}

}
