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
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.Port;
import org.jclouds.openstack.neutron.v2.features.PortApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
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
import org.mqnaas.extensions.openstack.jclouds.clientprovider.IJCloudsNeutronClientProvider;
import org.mqnaas.extensions.openstack.jclouds.clientprovider.IJCloudsNovaClientProvider;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.mqnaas.network.api.topology.port.IPortManagement;
import org.mqnaas.network.impl.topology.port.PortResource;
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
	private static final String		RESOURCE_URI				= "http://www.myfakeresource.com/";
	// user to create credentials of the ersource the capability is bound to.
	private final static String		PASSWORD					= "1234";

	// constants user to create the mocked client response
	private final static String		ZONE_A						= "RegionOne";
	private final static String		ZONE_B						= "RegionTwo";

	private final static String		SERVER_ONE_ID				= "23424-234346-13";
	private final static String		SERVER_TWO_ID				= "76878-123323-54";
	private final static String		SERVER_THREE_ID				= "54353-742572-37";

	private final static String		SERVER_ONE_NAME				= "server1";
	private final static String		SERVER_TWO_NAME				= "server2";
	private final static String		SERVER_THREE_NAME			= "server3";

	private final static String		SERVER_ONE_PORT_ONE_ID		= "1343-123-334";
	private final static String		SERVER_ONE_PORT_TWO_ID		= "9393-246-161";
	private final static String		SERVER_THREE_PORT_ONE_ID	= "4375-926-711";

	private final static String		SERVER_ONE_PORT_ONE_NAME	= "port-1-1";
	private final static String		SERVER_ONE_PORT_TWO_NAME	= "port-1-2";
	private final static String		SERVER_THREE_PORT_ONE_NAME	= "port-3-1";

	// required by JClouds to build "server" object
	private static final String		TENANT_ID					= "tenant-1234";
	private static final String		USER_ID						= "user-1234";

	/**
	 * AttributeStores capabilities for the VMs and ports returned by mocked service provider
	 */
	IAttributeStore					attributeStoreServerOne;
	IAttributeStore					attributeStoreServerTwo;
	IAttributeStore					attributeStoreServerThree;
	IAttributeStore					attributeStoreServerOnePortOne;
	IAttributeStore					attributeStoreServerOnePortTwo;
	IAttributeStore					attributeStoreServerThreePortOne;

	/**
	 * Resource injected in capability.
	 */
	IRootResource					openstackResource;

	/**
	 * Capability to be tested
	 */
	IRootResourceProvider			openstackRootResourceProvider;

	/**
	 * All capabilities dependencies (will be mocked)
	 */
	IServiceProvider				mockedServiceProvider;
	IJCloudsNovaClientProvider		mockedJcloudsNovaClientProvider;
	IJCloudsNeutronClientProvider	mockedJcloudsNeutronClientProvider;

	NeutronApi						mockedNeutronApi;
	NovaApi							mockedNovaApi;
	IClientProviderFactory			mockedClientProviderFactory;
	PortApi							mockedPortApiZoneA;
	PortApi							mockedPortApiZoneB;

	IPortManagement					mockedPortManagementCapab;

	PortResource					portResourceServerOnePortOne;
	PortResource					portResourceServerOnePortTwo;
	PortResource					portResourceServerThreePortOne;

	@Before
	public void prepareTest() throws EndpointNotFoundException, SecurityException, IllegalArgumentException, IllegalAccessException,
			CapabilityNotFoundException, InstantiationException, URISyntaxException, ProviderNotFoundException {
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

		checkPortAttributeStore(attributeStoreServerOnePortOne, SERVER_ONE_PORT_ONE_ID, SERVER_ONE_PORT_ONE_NAME);
		checkPortAttributeStore(attributeStoreServerOnePortTwo, SERVER_ONE_PORT_TWO_ID, SERVER_ONE_PORT_TWO_NAME);
		checkPortAttributeStore(attributeStoreServerThreePortOne, SERVER_THREE_PORT_ONE_ID, SERVER_THREE_PORT_ONE_NAME);

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

	private void checkPortAttributeStore(IAttributeStore portAttributeStore, String portExternalId, String portExternalName) {

		Assert.assertNotNull("AttributeStore should contain the external id of port.",
				portAttributeStore.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("Port representation should contain the externalId " + portExternalId, portExternalId,
				portAttributeStore.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));

		Assert.assertNotNull("AttributeStore should contain the external name of port.",
				portAttributeStore.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME));
		Assert.assertEquals("Port representation should contain the externalName " + portExternalName,
				portAttributeStore.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME), portExternalName);
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
	 * 
	 * @throws ProviderNotFoundException
	 */
	private void mockCapabilityDependencies() throws EndpointNotFoundException, SecurityException, IllegalArgumentException, IllegalAccessException,
			CapabilityNotFoundException, InstantiationException, URISyntaxException, ProviderNotFoundException {

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

		PowerMockito.when(serverApiRegionOneServers.concat()).thenReturn(new dummyFluentIterable<Server>(serverOne, serverTwo));
		PowerMockito.when(serverApiRegionTwoServers.concat()).thenReturn(new dummyFluentIterable<Server>(serverThree));

		// mock resourceManagementListener
		IResourceManagementListener rmListener = PowerMockito.mock(IResourceManagementListener.class);
		ReflectionTestHelper.injectPrivateField(openstackRootResourceProvider, rmListener, "resourceManagementListener");

		// mock PortAPI
		mockedPortApiZoneA = PowerMockito.mock(PortApi.class);
		mockedPortApiZoneB = PowerMockito.mock(PortApi.class);

		// mock PortAPI.getPorts
		Port port1 = buildPortObject(SERVER_ONE_PORT_ONE_ID, SERVER_ONE_PORT_ONE_NAME, SERVER_ONE_ID);
		Port port2 = buildPortObject(SERVER_ONE_PORT_TWO_ID, SERVER_ONE_PORT_TWO_NAME, SERVER_ONE_ID);
		Port port3 = buildPortObject(SERVER_THREE_PORT_ONE_ID, SERVER_THREE_PORT_ONE_NAME, SERVER_THREE_ID);

		PagedIterable<Port> mockedIterableZoneA = PowerMockito.mock(PagedIterable.class);
		PagedIterable<Port> mockedIterableZoneB = PowerMockito.mock(PagedIterable.class);
		PowerMockito.when(mockedPortApiZoneA.list()).thenReturn(mockedIterableZoneA);
		PowerMockito.when(mockedPortApiZoneB.list()).thenReturn(mockedIterableZoneB);
		PowerMockito.when(mockedIterableZoneA.concat()).thenReturn(new dummyFluentIterable<Port>(port1, port2));
		PowerMockito.when(mockedIterableZoneB.concat()).thenReturn(new dummyFluentIterable<Port>(port3));

		// mock NeutronAPI
		mockedNeutronApi = PowerMockito.mock(NeutronApi.class);
		PowerMockito.when(mockedNeutronApi.getPortApi(Mockito.eq(ZONE_A))).thenReturn(mockedPortApiZoneA);
		PowerMockito.when(mockedNeutronApi.getPortApi(Mockito.eq(ZONE_B))).thenReturn(mockedPortApiZoneB);

		// mock ports attributeStores
		attributeStoreServerOnePortOne = new AttributeStore();
		attributeStoreServerOnePortTwo = new AttributeStore();
		attributeStoreServerThreePortOne = new AttributeStore();
		ReflectionTestHelper.injectPrivateField(attributeStoreServerOnePortOne, new HashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStoreServerOnePortTwo, new HashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStoreServerThreePortOne, new HashMap<String, String>(), "attributes");

		// mock server attributeStores
		attributeStoreServerOne = new AttributeStore();
		attributeStoreServerTwo = new AttributeStore();
		attributeStoreServerThree = new AttributeStore();
		ReflectionTestHelper.injectPrivateField(attributeStoreServerOne, new HashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStoreServerTwo, new HashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStoreServerThree, new HashMap<String, String>(), "attributes");

		// mock jCloudsClientProvider and inject it in capability.
		mockedJcloudsNovaClientProvider = PowerMockito.mock(IJCloudsNovaClientProvider.class);
		mockedJcloudsNeutronClientProvider = PowerMockito.mock(IJCloudsNeutronClientProvider.class);

		mockedClientProviderFactory = PowerMockito.mock(IClientProviderFactory.class);
		PowerMockito.when(mockedClientProviderFactory.getClientProvider(Mockito.eq(IJCloudsNovaClientProvider.class))).thenReturn(
				mockedJcloudsNovaClientProvider);
		PowerMockito.when(mockedJcloudsNovaClientProvider.getClient(Mockito.eq(openstackResource))).thenReturn(mockedNovaApi);
		PowerMockito.when(mockedClientProviderFactory.getClientProvider(Mockito.eq(IJCloudsNeutronClientProvider.class))).thenReturn(
				mockedJcloudsNeutronClientProvider);
		PowerMockito.when(mockedJcloudsNeutronClientProvider.getClient(Mockito.eq(openstackResource))).thenReturn(mockedNeutronApi);
		ReflectionTestHelper.injectPrivateField(openstackRootResourceProvider, mockedClientProviderFactory, "clientProviderFactory");

		// mock portManagement capabilities
		portResourceServerOnePortOne = PowerMockito.mock(PortResource.class);
		portResourceServerOnePortTwo = PowerMockito.mock(PortResource.class);
		portResourceServerThreePortOne = PowerMockito.mock(PortResource.class);

		mockedPortManagementCapab = PowerMockito.mock(IPortManagement.class);
		PowerMockito.when(mockedPortManagementCapab.createPort()).thenReturn(portResourceServerOnePortOne)
				.thenReturn(portResourceServerOnePortTwo).thenReturn(portResourceServerThreePortOne);

		// mock and inject service provider
		IServiceProvider serviceProvider = PowerMockito.mock(IServiceProvider.class);

		PowerMockito.when(serviceProvider.getCapability(Mockito.any(IRootResource.class), Mockito.eq(IAttributeStore.class)))
				.thenReturn(attributeStoreServerOne).thenReturn(attributeStoreServerTwo).thenReturn(attributeStoreServerThree);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(portResourceServerOnePortOne), Mockito.eq(IAttributeStore.class))).thenReturn(
				attributeStoreServerOnePortOne);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(portResourceServerOnePortTwo), Mockito.eq(IAttributeStore.class))).thenReturn(
				attributeStoreServerOnePortTwo);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(portResourceServerThreePortOne), Mockito.eq(IAttributeStore.class))).thenReturn(
				attributeStoreServerThreePortOne);
		PowerMockito.when(serviceProvider.getCapability(Mockito.any(IRootResource.class), Mockito.eq(IPortManagement.class))).thenReturn(
				mockedPortManagementCapab);
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
	 * Dummy implementation of the {@link FluentIterable} abstract class. This abstract belongs to JClouds client and its used to iterate Resources.
	 * This implementation is for testing purposes.
	 * 
	 * @author Adrian Rosello Rey (i2CAT)
	 * @param <T>
	 *
	 */
	private class dummyFluentIterable<T> extends FluentIterable<T> {

		List<T>	instances;

		public dummyFluentIterable(T... object) {
			instances = Arrays.asList(object);
		}

		@Override
		public Iterator iterator() {
			return instances.iterator();
		}

	}

	private Port buildPortObject(String id, String name, String deviceId) throws SecurityException, IllegalArgumentException, IllegalAccessException {
		Port port = Port.createBuilder("network-1").deviceId(deviceId).name(name).build();
		// Neither port object nor port builder does offer methods to set id
		ReflectionTestHelper.injectPrivateField(port, id, "id");

		return port;
	}

}
