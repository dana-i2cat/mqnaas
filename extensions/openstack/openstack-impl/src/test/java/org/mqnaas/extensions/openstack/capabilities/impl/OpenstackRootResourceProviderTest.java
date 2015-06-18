package org.mqnaas.extensions.openstack.capabilities.impl;

/*
 * #%L
 * MQNaaS :: OpenStack Implementation
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.cxf.common.util.SortedArraySet;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.credentials.Credentials;
import org.mqnaas.core.api.credentials.UsernamePasswordTenantCredentials;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.impl.AttributeStore;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.extensions.openstack.jclouds.clientprovider.IJCloudsNovaClientProvider;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.powermock.api.mockito.PowerMockito;

import com.google.common.collect.FluentIterable;

/**
 * <p>
 * Class containing tests for the {@link OpenstackRootResourceProvider} capability implementation.
 * </p>
 * 
 * @author Adrian Rosello Rey (i2CAT)
 *
 */
public class OpenstackRootResourceProviderTest {

	// used to create the endpoint of the resource the capability is bound to.
	private static final String	RESOURCE_URI		= "http://www.myfakeresource.com/";
	// user to create credentials of the ersource the capability is bound to.
	private final static String	PASSWORD			= "1234";

	// constants user to create the mocked client response
	private final static String	ZONE_A				= "RegionOne";
	private final static String	ZONE_B				= "RegionTwo";

	private final static String	SERVER_ONE_ID		= "23424-234346-13";
	private final static String	SERVER_TWO_ID		= "76878-123323-54";
	private final static String	SERVER_THREE_ID		= "54353-742572-37";

	private final static String	SERVER_ONE_NAME		= "server1";
	private final static String	SERVER_TWO_NAME		= "server2";
	private final static String	SERVER_THREE_NAME	= "server3";

	// required by JClouds to build "server" object
	private static final String	TENANT_ID			= "tenant-1234";
	private static final String	USER_ID				= "user-1234";

	/**
	 * AttributeStores capabilities for the VMs returned by mocked service provider
	 */
	IAttributeStore				attributeStoreServerOne;
	IAttributeStore				attributeStoreServerTwo;
	IAttributeStore				attributeStoreServerThree;
	/**
	 * Resource injected in capability.
	 */
	IRootResource				openstackResource;

	/**
	 * Capability to be tested
	 */
	IRootResourceProvider		openstackRootResourceProvider;

	/**
	 * All capabilities dependencies (will be mocked)
	 */
	IServiceProvider			mockedServiceProvider;
	IJCloudsNovaClientProvider	mockedJcloudsClientProvider;
	NovaApi						mockedNovaApi;

	@Before
	public void prepareTest() throws EndpointNotFoundException, SecurityException, IllegalArgumentException, IllegalAccessException,
			CapabilityNotFoundException, InstantiationException, URISyntaxException {
		openstackRootResourceProvider = new OpenstackRootResourceProvider();
		mockCapabilityDependencies();
	}

	/**
	 * Tests the {@link OpenstackRootResourceProvider#activate()} method, which instantiates the {@link IRootResource}s representing the Openstack
	 * VMs. Client simulates Openstack contains three {@link Server}s in two different zones, so 3 IRootResources should be created,containing its
	 * metadata in {@link IAttributeStore} capability
	 */
	@Test
	public void activateTest() throws ApplicationActivationException, CapabilityNotFoundException {

		openstackRootResourceProvider.activate();
		List<IRootResource> vms = openstackRootResourceProvider.getRootResources();

		Assert.assertNotNull("OpenstackRootResourceProvider capability should contain virtual machines.", vms);
		Assert.assertFalse("OpenstackRootResourceProvider capability should contain virtual machines.", vms.isEmpty());
		Assert.assertEquals("OpenstackRootResourceProvider capability should contain virtual machines.", 3, vms.size());

		IRootResource rootResourceOne = vms.get(0);
		IRootResource rootResourceTwo = vms.get(1);
		IRootResource rootResourceThree = vms.get(2);

		checkResourceSpecification(rootResourceOne);
		checkResourceSpecification(rootResourceTwo);
		checkResourceSpecification(rootResourceThree);

		checkResouceAttributeStore(attributeStoreServerOne, SERVER_ONE_ID, SERVER_ONE_NAME, ZONE_A);
		checkResouceAttributeStore(attributeStoreServerTwo, SERVER_TWO_ID, SERVER_TWO_NAME, ZONE_A);
		checkResouceAttributeStore(attributeStoreServerThree, SERVER_THREE_ID, SERVER_THREE_NAME, ZONE_B);

	}

	/**
	 * Checks that the {@link IAttributeStore} capability of a specific openstack VM representation contains the external id, name and zone it belongs
	 * to.
	 */
	private void checkResouceAttributeStore(IAttributeStore resourceAttributeStore, String serverId, String serverName, String zone) {

		Assert.assertNotNull("AttributeStore should contain the external id of VM resource.",
				resourceAttributeStore.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("VM representation should contain the externalId " + serverId,
				resourceAttributeStore.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID), serverId);

		Assert.assertNotNull("AttributeStore should contain the external name of VM resource.",
				resourceAttributeStore.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME));
		Assert.assertEquals("VM representation should contain the externalName " + serverName,
				resourceAttributeStore.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME), serverName);

		Assert.assertNotNull("AttributeStore should contain the zone the VM resource belongs to.",
				resourceAttributeStore.getAttribute(OpenstackRootResourceProvider.ZONE_ATTRIBUTE));

		Assert.assertEquals("VM representation should belong to zone " + zone,
				resourceAttributeStore.getAttribute(OpenstackRootResourceProvider.ZONE_ATTRIBUTE), zone);
	}

	/**
	 * Checks that the given {@link IRootResource}, representing an Openstack VM, contains correct {@link Specification}, and that its
	 * {@link Credentials} and {@link Endpoint}s have been inherited from parent resource.
	 */
	private void checkResourceSpecification(IRootResource rootResource) {

		Assert.assertNotNull("VM Resource representation should contain a descriptor.", rootResource.getDescriptor());
		Assert.assertNotNull("VM Resource representation should contain a descriptor", rootResource.getDescriptor().getSpecification());

		Assert.assertEquals("VM Resource representation should be of type host.", Type.HOST, rootResource.getDescriptor().getSpecification()
				.getType());
		Assert.assertEquals("VM Resource representation should be of model \"openstack\".", "openstack", rootResource.getDescriptor()
				.getSpecification().getModel());

		Assert.assertNotNull("VM Resource representation should contain a list of endpoints", rootResource.getDescriptor().getEndpoints());
		Assert.assertEquals("VM Resource representation should inherit endpoints from parent resource.", openstackResource.getDescriptor()
				.getEndpoints(), rootResource.getDescriptor().getEndpoints());

		Assert.assertEquals("VM Resource representation should inherit credentials from parent resource.", openstackResource.getDescriptor()
				.getCredentials(), rootResource.getDescriptor().getCredentials());

	}

	/**
	 * Mock and inject required capabilities and resources in the tested capability.
	 */
	private void mockCapabilityDependencies() throws EndpointNotFoundException, SecurityException, IllegalArgumentException, IllegalAccessException,
			CapabilityNotFoundException, InstantiationException, URISyntaxException {

		// create and inject resource in capability

		Specification spec = new Specification(Type.CLOUD_MANAGER, "openstack");
		Endpoint fakeEndpoint = new Endpoint(new URI(RESOURCE_URI));
		UsernamePasswordTenantCredentials credentials = new UsernamePasswordTenantCredentials(USER_ID, PASSWORD, TENANT_ID);

		openstackResource = new RootResource(RootResourceDescriptor.create(spec, Arrays.asList(fakeEndpoint), credentials));
		ReflectionTestHelper.injectPrivateField(openstackRootResourceProvider, openstackResource, "resource");

		// mock NovaApi.getConfigurezZones() responses
		Set<String> zones = new SortedArraySet<String>();
		zones.add(ZONE_A);
		zones.add(ZONE_B);
		mockedNovaApi = PowerMockito.mock(NovaApi.class);
		PowerMockito.when(mockedNovaApi.getConfiguredZones()).thenReturn(zones);

		// mock NovaApi.getServerAPI for Zone
		ServerApi serverApiRegionOne = PowerMockito.mock(ServerApi.class);
		ServerApi serverApiRegionTwo = PowerMockito.mock(ServerApi.class);
		PowerMockito.when(mockedNovaApi.getServerApiForZone(Mockito.eq(ZONE_A))).thenReturn(serverApiRegionOne);
		PowerMockito.when(mockedNovaApi.getServerApiForZone(Mockito.eq(ZONE_B))).thenReturn(serverApiRegionTwo);

		Server serverOne = buildServerObject(SERVER_ONE_ID, SERVER_ONE_NAME);
		Server serverTwo = buildServerObject(SERVER_TWO_ID, SERVER_TWO_NAME);
		Server serverThree = buildServerObject(SERVER_THREE_ID, SERVER_THREE_NAME);

		PagedIterable<Server> serverApiRegionOneServers = PowerMockito.mock(PagedIterable.class);
		PagedIterable<Server> serverApiRegionTwoServers = PowerMockito.mock(PagedIterable.class);

		PowerMockito.when(serverApiRegionOne.listInDetail()).thenReturn(serverApiRegionOneServers);
		PowerMockito.when(serverApiRegionTwo.listInDetail()).thenReturn(serverApiRegionTwoServers);

		PowerMockito.when(serverApiRegionOneServers.concat()).thenReturn(new dummyServerFluentIterable(serverOne, serverTwo));
		PowerMockito.when(serverApiRegionTwoServers.concat()).thenReturn(new dummyServerFluentIterable(serverThree));

		// mock resourceManagementListener
		IResourceManagementListener rmListener = PowerMockito.mock(IResourceManagementListener.class);
		ReflectionTestHelper.injectPrivateField(openstackRootResourceProvider, rmListener, "resourceManagementListener");

		// mock attributeStores
		attributeStoreServerOne = new AttributeStore();
		attributeStoreServerTwo = new AttributeStore();
		attributeStoreServerThree = new AttributeStore();
		ReflectionTestHelper.injectPrivateField(attributeStoreServerOne, new HashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStoreServerTwo, new HashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStoreServerThree, new HashMap<String, String>(), "attributes");

		// mock jCloudsClientProvider and inject it in capability.
		mockedJcloudsClientProvider = PowerMockito.mock(IJCloudsNovaClientProvider.class);
		PowerMockito.when(mockedJcloudsClientProvider.getClient(Mockito.eq(openstackResource))).thenReturn(mockedNovaApi);

		ReflectionTestHelper.injectPrivateField(openstackRootResourceProvider, mockedJcloudsClientProvider, "jcloudsClientProvider");

		// mock and inject service provider

		IServiceProvider serviceProvider = PowerMockito.mock(IServiceProvider.class);
		PowerMockito.when(serviceProvider.getCapability(Mockito.any(IRootResource.class), Mockito.eq(IAttributeStore.class)))
				.thenReturn(attributeStoreServerOne).thenReturn(attributeStoreServerTwo).thenReturn(attributeStoreServerThree);
		ReflectionTestHelper.injectPrivateField(openstackRootResourceProvider, serviceProvider, "serviceProvider");

	}

	/**
	 * Builds a {@link Server} instance with the specificed server id and name, and adds all required information by JClouds to build a server
	 * instance.
	 */
	private Server buildServerObject(String serverId, String serverName) {
		return Server.builder().id(serverId).name(serverName).tenantId(TENANT_ID).userId(USER_ID)
				.created(new Date(System.currentTimeMillis())).status(Status.ACTIVE).flavor(PowerMockito.mock(Resource.class)).build();
	}

	/**
	 * Dummy implementation of the {@link FluentIterable} abstract class. This abstract belongs to JClouds client and its used to iterate over
	 * Resources. This implementation is for testing purposes and contains a list of {@link Server}s.
	 * 
	 * @author Adrian Rosello Rey (i2CAT)
	 *
	 */
	private class dummyServerFluentIterable extends FluentIterable<Server> {

		List<Server>	servers;

		public dummyServerFluentIterable(Server... server) {
			servers = Arrays.asList(server);
		}

		@Override
		public Iterator iterator() {
			return servers.iterator();
		}

	}

}
