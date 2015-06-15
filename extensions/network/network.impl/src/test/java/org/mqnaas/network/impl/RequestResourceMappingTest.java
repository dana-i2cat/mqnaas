package org.mqnaas.network.impl;

/*
 * #%L
 * MQNaaS :: Network Implementation
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.core.api.slicing.IUnitAdministration;
import org.mqnaas.core.api.slicing.IUnitManagement;
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.core.impl.slicing.SliceResource;
import org.mqnaas.core.impl.slicing.UnitAdministration;
import org.mqnaas.core.impl.slicing.UnitManagment;
import org.mqnaas.core.impl.slicing.UnitResource;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.mqnaas.network.api.request.IRequestResourceMapping;
import org.mqnaas.network.impl.request.RequestResource;
import org.mqnaas.network.impl.request.RequestResourceMapping;
import org.powermock.api.mockito.PowerMockito;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 *
 */
public class RequestResourceMappingTest {

	private static final String	PORT_UNIT_NAME	= "port";
	private static final String	VLAN_UNIT_NAME	= "vlan";

	IRequestResourceMapping		requestResourceMapping;

	IServiceProvider			serviceProvider;

	RequestResource				request;
	IRootResource				rootResource;

	@Before
	public void prepareTest() throws InstantiationException, IllegalAccessException, URISyntaxException, ApplicationActivationException {

		Endpoint endpoint = new Endpoint(new URI("http://www.myfakeresource.com"));
		rootResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.OF_SWITCH), Arrays.asList(endpoint)));

		request = new RequestResource();

		requestResourceMapping = new RequestResourceMapping();
		requestResourceMapping.activate();

		serviceProvider = PowerMockito.mock(IServiceProvider.class);
		ReflectionTestHelper.injectPrivateField(requestResourceMapping, serviceProvider, "serviceProvider");

	}

	@Test
	public void isSupportingTest() {
		Assert.assertTrue("RequestResourceMapping capability should bind to RequestResource instances.", RequestResourceMapping.isSupporting(request));
		Assert.assertFalse("RequestResourceMapping capability should only bind to RequestResource instances.",
				RequestResourceMapping.isSupporting(rootResource));

		Assert.assertFalse("RequestResourceMapping capability should only bind to RequestResource instances.",
				RequestResourceMapping.isSupporting(new IResource() {
					@Override
					public String getId() {
						// TODO Auto-generated method stub
						return null;
					}
				}));

	}

	@Test
	public void defineMappingTest() throws CapabilityNotFoundException, SecurityException, IllegalArgumentException, IllegalAccessException,
			ApplicationActivationException {

		// 1) create mock SliceProviders and mock service provider to return them
		ISliceProvider rootResourceSliceProvider = PowerMockito.mock(ISliceProvider.class);
		ISliceProvider reqSliceProvider = PowerMockito.mock(ISliceProvider.class);

		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(rootResource), Mockito.eq(ISliceProvider.class))).thenReturn(
				rootResourceSliceProvider);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(request), Mockito.eq(ISliceProvider.class))).thenReturn(reqSliceProvider);

		// 2) create slice resources
		IResource rootResourceSlice = new SliceResource();
		IResource reqSlice = new SliceResource();

		// 3) mock sliceProviders capabilities to return sliced resources
		PowerMockito.when(rootResourceSliceProvider.getSlice()).thenReturn(rootResourceSlice);
		PowerMockito.when(reqSliceProvider.getSlice()).thenReturn(reqSlice);

		// 4) create unit management capabilities and mock service provider to return them
		IUnitManagement reqUnitMgmt = new UnitManagment();
		IUnitManagement rootResourceUnitMgmt = new UnitManagment();
		ReflectionTestHelper.injectPrivateField(reqUnitMgmt, new dummyResource(), "resource");
		ReflectionTestHelper.injectPrivateField(rootResourceUnitMgmt, new dummyResource(), "resource");
		reqUnitMgmt.activate();
		rootResourceUnitMgmt.activate();
		IResource portUnit = rootResourceUnitMgmt.createUnit(PORT_UNIT_NAME);
		IResource vlanUnit = rootResourceUnitMgmt.createUnit(VLAN_UNIT_NAME);

		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(reqSlice), Mockito.eq(IUnitManagement.class))).thenReturn(reqUnitMgmt);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(rootResourceSlice), Mockito.eq(IUnitManagement.class))).thenReturn(
				rootResourceUnitMgmt);

		// 5)) create unit administrations and mock service provider to return them
		IUnitAdministration rootResourcePortAdmin = new UnitAdministration();
		ReflectionTestHelper.injectPrivateField(rootResourcePortAdmin, new dummyResource(), "resource");
		rootResourcePortAdmin.activate();
		rootResourcePortAdmin.setRange(new Range(0, 2));

		IUnitAdministration rootResourceVlanAdmin = new UnitAdministration();
		ReflectionTestHelper.injectPrivateField(rootResourceVlanAdmin, new dummyResource(), "resource");
		rootResourceVlanAdmin.activate();
		rootResourceVlanAdmin.setRange(new Range(0, 4095));

		IUnitAdministration reqVlanAdmin = new UnitAdministration();
		ReflectionTestHelper.injectPrivateField(reqVlanAdmin, new dummyResource(), "resource");
		reqVlanAdmin.activate();

		IUnitAdministration reqPortAdmin = new UnitAdministration();
		ReflectionTestHelper.injectPrivateField(reqPortAdmin, new dummyResource(), "resource");
		reqPortAdmin.activate();

		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(portUnit), Mockito.eq(IUnitAdministration.class))).thenReturn(
				rootResourceVlanAdmin);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(vlanUnit), Mockito.eq(IUnitAdministration.class))).thenReturn(
				rootResourcePortAdmin);
		PowerMockito
				.when(serviceProvider.getCapability(AdditionalMatchers.not(AdditionalMatchers.or(Mockito.eq(portUnit), Mockito.eq(vlanUnit))),
						Mockito.eq(IUnitAdministration.class))).thenReturn(reqVlanAdmin).thenReturn(reqPortAdmin);

		// 6) mock service provider, so the resource can be seen as sliceable
		PowerMockito.when(serviceProvider.getCapability(Mockito.any(IResource.class), Mockito.eq(ISlicingCapability.class))).thenReturn(
				PowerMockito.mock(ISlicingCapability.class));

		// Assert before method execution
		Assert.assertTrue("Virtual resource should not contain any slicing unit yet.", reqUnitMgmt.getUnits().isEmpty());
		Assert.assertTrue("Capability should not contain any mapped resource yet.", requestResourceMapping.getMappedDevices().isEmpty());
		Assert.assertNull("Capability should not contain any mapped resource yet.", requestResourceMapping.getMapping(request));

		// METHOD TESTED
		requestResourceMapping.defineMapping(request, rootResource);

		// Asserts after method execution
		Assert.assertFalse("Capability should  contain one mapped resource.", requestResourceMapping.getMappedDevices().isEmpty());
		Assert.assertEquals("Capability should contain one mapped resource.", 1, requestResourceMapping.getMappedDevices().size());
		Assert.assertEquals("Capability should contain one mapped resource.", request, requestResourceMapping.getMappedDevices().iterator()
				.next());
		Assert.assertEquals("Capability should contain the mapping of the request resource.", rootResource,
				requestResourceMapping.getMapping(request));

		Assert.assertFalse("Virtual resource should contain slicing units.", reqUnitMgmt.getUnits().isEmpty());
		Assert.assertEquals("Virtual resource should contain two slicing units.", 2, reqUnitMgmt.getUnits().size());

		IResource reqPortUnit = (((UnitResource) reqUnitMgmt.getUnits().get(0)).getName() == PORT_UNIT_NAME ? reqUnitMgmt.getUnits().get(0) : reqUnitMgmt
				.getUnits().get(1));
		IResource reqVlanUnit = (((UnitResource) reqUnitMgmt.getUnits().get(0)).getName() == VLAN_UNIT_NAME ? reqUnitMgmt.getUnits().get(0) : reqUnitMgmt
				.getUnits().get(1));
		Assert.assertFalse("There should port and vlan slicing units in the virtual resource.", reqPortUnit.equals(reqVlanUnit));

		Assert.assertEquals(new Range(0, 2), reqPortAdmin.getRange());
		Assert.assertEquals(new Range(0, 4095), reqVlanAdmin.getRange());

	}

	private class dummyResource implements IResource {

		@Override
		public String getId() {
			return "dummyId";
		}

	}
}
