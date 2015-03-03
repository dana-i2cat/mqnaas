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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.IBindingDecider;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.impl.dummy.DummyBundleGuard;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
// needed to mock static method of FrameworkUtil class.
@RunWith(PowerMockRunner.class)
@PrepareForTest(FrameworkUtil.class)
public class ServiceProviderImplTest {

	static RootResourceManagement	resourceManagement;
	static BindingManagement		bindingManagement;
	static CoreProvider				coreProvider;

	@BeforeClass
	public static void init() throws Exception {

		resourceManagement = new RootResourceManagement();
		coreProvider = new CoreProvider();

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
		bindingManagement.setCoreProvider(coreProvider);
		// use dummy BundleGuard
		bindingManagement.setBundleGuard(new DummyBundleGuard());

		bindingManagement.init();

		List<Class<? extends ICapability>> knownCapabilities = new ArrayList<Class<? extends ICapability>>();
		knownCapabilities.add(BindingManagement.class);
		knownCapabilities.add(RootResourceManagement.class);
		knownCapabilities.add(bindingDecider.getClass());
		knownCapabilities.add(ExecutionService.class);
		knownCapabilities.add(Sample2Capability.class);
		bindingManagement.capabilitiesAdded(knownCapabilities);
	}

	/**
	 * Tests {@link BindingManagement} implementation for {@link IServiceProvider}
	 * 
	 * @throws ResourceNotFoundException
	 * @throws ServiceNotFoundException
	 */
	@Test
	public void getServicesTest() throws ResourceNotFoundException, ServiceNotFoundException {

		IResource resource = coreProvider.getCore();

		CapabilityInstance ci = getCapabilityInstanceBoundToResource(resource, Sample2Capability.class);
		Assert.assertNotNull("Sample2Capability should be bound to the resource", ci);

		Collection<IService> sample2Services = bindingManagement.getServices(resource).get(ISample2Capability.class);
		Assert.assertNotNull("getServices should contain services in ISample2Capability ", sample2Services);
		Assert.assertFalse("getServices should contain services in ISample2Capabilit", sample2Services.isEmpty());

		for (Method method : ISample2Capability.class.getMethods()) {
			IService found = null;
			for (IService service : sample2Services) {
				if (service.getMetadata().getMethod().equals(method))
					found = service;
			}
			Assert.assertNotNull("There should be a service for method " + method, found);
		}

		for (Method method : ISample2Capability.class.getMethods()) {
			IService retrieved = bindingManagement.getService(resource, method.getName(), method.getParameterTypes());
			Assert.assertNotNull("getService finds a service for method " + method);

			Assert.assertEquals("getService finds correct service for method: " + method, method, retrieved.getMetadata().getMethod());
			Assert.assertEquals("getService finds correct service for method (name check): " + method, method.getName(), retrieved.getMetadata()
					.getName());
			Assert.assertTrue("getService finds correct service for method (parameters check): " + method,
					Arrays.equals(method.getParameterTypes(), retrieved.getMetadata().getParameterTypes()));
		}

	}

	@Test
	public void getCapabilityTest() throws ResourceNotFoundException, CapabilityNotFoundException {
		IResource resource = resourceManagement.getRootResource(new Specification(Type.CORE));

		IRootResourceAdministration resourceAdmin = bindingManagement.getCapability(resource, IRootResourceAdministration.class);
		Assert.assertNotNull("Core resource should contain a bound IRootResourceAdministration capability", resourceAdmin);

		IRootResourceProvider resourceProvider = bindingManagement.getCapability(resource, IRootResourceProvider.class);
		Assert.assertNotNull("Core resource should contain a bound IRootResourceProvier capability", resourceProvider);

		ISample2Capability sampleCapab = bindingManagement.getCapability(resource, ISample2Capability.class);
		Assert.assertNotNull("Core resource should contain a bound ISample2Capability capability", sampleCapab);

	}

	@Test(expected = CapabilityNotFoundException.class)
	public void getUnboundCapabilityTest() throws CapabilityNotFoundException, ResourceNotFoundException {
		IResource resource = resourceManagement.getRootResource(new Specification(Type.CORE));

		bindingManagement.getCapability(resource, ISampleMgmtCapability.class);
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

}
