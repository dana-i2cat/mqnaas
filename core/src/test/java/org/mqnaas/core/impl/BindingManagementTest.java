package org.mqnaas.core.impl;

/*
 * #%L
 * MQNaaS :: Core
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IBindingDecider;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.exceptions.ApplicationNotFoundException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.impl.dummy.DummyBundleGuard;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceCapabilityTreeController;
import org.mqnaas.core.impl.resourcetree.ResourceNode;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * http://www.codeproject.com/Articles/806508/Using-PowerMockito-to-Mock-Final-and-Static-Method
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
// needed to mock static method of FrameworkUtil class.
@RunWith(PowerMockRunner.class)
@PrepareForTest(FrameworkUtil.class)
public class BindingManagementTest {

	static RootResourceManagement	resourceManagement;
	static CoreProvider				coreProvider;
	static BindingManagement		bindingManagement;

	@BeforeClass
	public static void init() throws Exception {

		resourceManagement = new RootResourceManagement();
		coreProvider = new CoreProvider();

		IBindingDecider bindingDecider = new IBindingDecider() {
			@Override
			public boolean shouldBeBound(IResource resource, Class<? extends ICapability> capabilityClass) {

				boolean shouldBeBound = resource instanceof IRootResource;

				if (!shouldBeBound) {
					shouldBeBound = ISampleCapability.class.isAssignableFrom(capabilityClass);
				}

				return shouldBeBound;
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
		bindingManagement.setCoreProvider(coreProvider);
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
	public void bindAndUnbindCapabilityInstanceToResource() throws ResourceNotFoundException {

		IResource core = coreProvider.getCore();

		CapabilityInstance ci = new CapabilityInstance(SampleCapability.class);
		CapabilityNode cn = new CapabilityNode(ci);
		bindingManagement.bind(cn, bindingManagement.getResourceCapabilityTree().getRootResourceNode());

		Assert.assertTrue("CI should be bound to the resource",
				bindingManagement.getCapabilityInstancesBoundToResource(core).contains(ci));

		Assert.assertNotNull("Services in ISampleCapability should be available for resource",
				bindingManagement.getServices(core).get(ISampleCapability.class));
		Assert.assertFalse("Services in ISampleCapability should be available for resource",
				bindingManagement.getServices(core).get(ISampleCapability.class).isEmpty());

		Object resourceValue = ((SampleCapability) ci.getInstance()).getResource();
		Assert.assertEquals("Resource must be injected in capability.", core, resourceValue);

		ResourceNode coreNode = bindingManagement.getResourceCapabilityTree().getRootResourceNode();

		ci = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		cn = ResourceCapabilityTreeController.getChidrenWithContent(coreNode, ci);

		bindingManagement.unbind(cn, coreNode);

		Assert.assertFalse("CI should NOT be bound to the resource",
				bindingManagement.getCapabilityInstancesBoundToResource(core).contains(ci));

		Assert.assertTrue("Services in ISampleCapability should NOT be available for resource",
				bindingManagement.getServices(core).get(ISampleCapability.class).isEmpty());
	}

	@Test
	public void addAndRemoveResourceInCapabilityInstance() throws ResourceNotFoundException, CapabilityNotFoundException,
			ApplicationNotFoundException {

		addSampleCapability();

		IResource core = coreProvider.getCore();

		CapabilityInstance sampleCI = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		Assert.assertNotNull(sampleCI);

		IResource sampleResource = generateSampleResource();

		bindingManagement.resourceAdded(sampleResource, sampleCI.getInstance(), ISampleCapability.class);

		Assert.assertTrue("SampleResource should be provided by SampleCapability",
				bindingManagement.getResourcesProvidedByCapabilityInstance(sampleCI).contains(sampleResource));

		ResourceNode root = bindingManagement.getResourceCapabilityTree().getRootResourceNode();
		CapabilityNode capability = ResourceCapabilityTreeController.getCapabilityNodeWithContent(root, sampleCI);

		ResourceNode sampleResourceNode = capability.getChildren().get(0);
		sampleResource = sampleResourceNode.getContent();

		bindingManagement.resourceRemoved(sampleResource, sampleCI.getInstance(), ISampleCapability.class);

		Assert.assertFalse("SampleResource should NOT provided by SampleCapability",
				bindingManagement.getResourcesProvidedByCapabilityInstance(sampleCI).contains(sampleResource));

		removeSampleCapability();
	}

	@Test
	public void newCapabilitiesAreAutomaticallyBoundToResources() throws ResourceNotFoundException {

		addSampleCapability();

		IResource core = coreProvider.getCore();

		// following check relies on bindingDecider.shouldBeBound(core, SampleCapability.class) returning true
		// which is the trigger for a CapabilityInstance with SampleCapability being bound to sampleResource
		CapabilityInstance sampleCIForCoreResource = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		Assert.assertNotNull("SampleCapability should be bound to the resource", sampleCIForCoreResource);

		Assert.assertNotNull("Services in ISampleCapability should be available for the resource",
				bindingManagement.getServices(core).get(ISampleCapability.class));
		Assert.assertFalse("Services in ISampleCapability should be available for the resource",
				bindingManagement.getServices(core).get(ISampleCapability.class).isEmpty());

		removeSampleCapability();
	}

	@Ignore
	@Test
	public void knownCapabilitiesAreAutomaticallyBoundToNewResources() throws ResourceNotFoundException, CapabilityNotFoundException,
			ApplicationNotFoundException {

		addSampleCapability();

		// add new resource to bindingManagement
		IResource core = coreProvider.getCore();
		CapabilityInstance sampleCI = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		Assert.assertNotNull(sampleCI);
		IResource sampleResource = generateSampleResource();
		bindingManagement.resourceAdded(sampleResource, sampleCI.getInstance(), ISampleCapability.class);

		// following check relies on bindingDecider.shouldBeBound(sampleResource, SampleCapability.class) returning true
		// which is the trigger for a CapabilityInstance with SampleCapability being bound to sampleResource
		CapabilityInstance sampleCIForSampleResource = getCapabilityInstanceBoundToResource(sampleResource, SampleCapability.class);
		Assert.assertNotNull("SampleCapability should be bound to the resource", sampleCIForSampleResource);

		Assert.assertNotNull("Services in ISampleCapability should be available for the resource",
				bindingManagement.getServices(sampleResource).get(ISampleCapability.class));
		Assert.assertFalse("Services in ISampleCapability should be available for the resource",
				bindingManagement.getServices(sampleResource).get(ISampleCapability.class).isEmpty());

		removeSampleCapability();
	}

	@Test
	public void capabilitiesAndResourcesAreUnboundInCascadeWhenResourceIsRemoved() throws ResourceNotFoundException, CapabilityNotFoundException,
			ApplicationNotFoundException {

		// adding SampleCapability used in this test
		addSampleCapability();

		// add new resource to bindingManagement
		IResource core = coreProvider.getCore();
		CapabilityInstance coreSampleCI = getCapabilityInstanceBoundToResource(core, SampleCapability.class);
		Assert.assertNotNull(coreSampleCI);

		// create a binding chain with 5 resources with a SampleCapability each.
		List<IResource> chainResources = new ArrayList<IResource>();
		List<CapabilityInstance> chainCapabilityInstances = new ArrayList<CapabilityInstance>();

		IResource resource;
		CapabilityInstance sampleCI = coreSampleCI;
		for (int i = 0; i < 5; i++) {
			resource = generateSampleResource();
			bindingManagement.resourceAdded(resource, sampleCI.getInstance(), ISampleCapability.class);
			sampleCI = getCapabilityInstanceBoundToResource(resource, SampleCapability.class);
			Assert.assertNotNull(sampleCI);

			chainResources.add(resource);
			chainCapabilityInstances.add(sampleCI);
		}

		// remove first resource in the chain
		IResource toRemove = chainResources.get(0);
		bindingManagement.resourceRemoved(toRemove, coreSampleCI.getInstance(), ISampleCapability.class);
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

		removeSampleCapability();
	}

	private static List<Class<? extends ICapability>>	sampleCapability;

	static {
		sampleCapability = new ArrayList<Class<? extends ICapability>>(1);
		sampleCapability.add(SampleCapability.class);
	}

	private static void addSampleCapability() {
		bindingManagement.capabilitiesAdded(sampleCapability);
	}

	private static void removeSampleCapability() {
		bindingManagement.capabilitiesRemoved(sampleCapability);
	}

	private static CapabilityInstance getCapabilityInstanceBoundToResource(IResource resource, Class<? extends ICapability> clazz) {
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
