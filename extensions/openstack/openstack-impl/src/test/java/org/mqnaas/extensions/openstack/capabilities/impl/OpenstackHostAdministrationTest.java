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

import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Server.Status;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.credentials.UsernamePasswordTenantCredentials;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.impl.AttributeStore;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.extensions.openstack.capabilities.host.api.IHostAdministration;
import org.mqnaas.extensions.openstack.jclouds.clientprovider.IJCloudsNovaClientProvider;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.powermock.api.mockito.PowerMockito;

/**
 * <p>
 * Unitary tests for the {@link OpenstackHostAdministration} capability.
 * </p>
 * 
 * @author Adrian Rosello Rey (i2CAT)
 *
 */
public class OpenstackHostAdministrationTest {

	// used to create the endpoint of the resource the capability is bound to.
	private static final String	RESOURCE_URI	= "http://www.myfakeresource.com/";

	// required to jclouds clientprovider to instantiate client.
	private static final String	TENANT_ID		= "tenant-1234";
	private static final String	USER_ID			= "user-1234";
	private static final String	PASSWORD		= "1234";

	// required for getting metadata in attributeStore
	private static final String	ZONE			= "zone-1";
	private static final String	HOST_EXT_ID		= "13422-452-1343454";
	private static final String	HOST_EXT_NAME	= "openstack-vm-1";

	// information to be returned by the mocked client
	private static final String	FLAVOR_ID		= "14723-123-2354361";
	private static final String	FLAVOR_NAME		= "flavor-1";

	private static final int	VM_RAM			= 4096;
	private static final int	V_CPUS			= 4;
	private static final int	VM_DISK			= 80;
	private static final String	SWAP_SIZE		= "2 GB";

	/**
	 * Capability to be tested
	 */
	IHostAdministration			openstackHostAdminCapability;

	/**
	 * Resource injected in tested capabilty
	 */
	IRootResource				hostResource;

	/**
	 * All capabilities dependencies (will be mocked)
	 */
	IServiceProvider			mockedServiceProvider;
	IJCloudsNovaClientProvider	mockedJcloudsClientProvider;
	NovaApi						mockedNovaApi;
	ServerApi					mockedServerApi;

	/**
	 * Objects returned by mocked capabilities/clients.
	 */
	IAttributeStore				attributeStore;
	Flavor						flavor;
	Server						server;

	@Before
	public void prepareTest() throws ApplicationActivationException, SecurityException, IllegalArgumentException, IllegalAccessException,
			InstantiationException, URISyntaxException, EndpointNotFoundException, ProviderNotFoundException {

		openstackHostAdminCapability = new OpenstackHostAdministration();

		mockAndCreateCapabilityDependencies();

		openstackHostAdminCapability.activate();

	}

	/**
	 * Test checks that all services of the {@link OpenstackHostAdministration} capability return the same value as the Jclouds client retrieved from
	 * Openstack instance.
	 */
	@Test
	public void capabilityServicesTest() {
		Assert.assertEquals("OpenstackHostAdministrationCapabiliy should return the same value the client provided.", V_CPUS,
				openstackHostAdminCapability.getNumberOfCpus());

		Assert.assertEquals("OpenstackHostAdministrationCapabiliy should return the same value the client provided.", VM_RAM,
				openstackHostAdminCapability.getMemorySize());

		Assert.assertEquals("OpenstackHostAdministrationCapabiliy should return the same value the client provided.", VM_DISK,
				openstackHostAdminCapability.getDiskSize());

		Assert.assertEquals("OpenstackHostAdministrationCapabiliy should return the same value the client provided.", SWAP_SIZE,
				openstackHostAdminCapability.getSwapSize());
	}

	/**
	 * Test checks that, if the {@link IAttributeStore} of the resource bound to the tested capability does not contain the resource external id, it
	 * fails with an {@link IllegalStateException}.
	 */
	@Test(expected = IllegalStateException.class)
	public void noStoredExternalId() throws SecurityException, IllegalArgumentException, IllegalAccessException {
		ReflectionTestHelper.injectPrivateField(attributeStore, new HashMap<String, String>(), "attributes");
		attributeStore.setAttribute(OpenstackRootResourceProvider.ZONE_ATTRIBUTE, ZONE);
		openstackHostAdminCapability.getNumberOfCpus();
	}

	/**
	 * Test checks that, if the {@link IAttributeStore} of the resource bound to the tested capability does not contain the Openstack zone it belongs
	 * to, it fails with an {@link IllegalStateException}.
	 */
	@Test(expected = IllegalStateException.class)
	public void noStoredZone() throws SecurityException, IllegalArgumentException, IllegalAccessException {
		ReflectionTestHelper.injectPrivateField(attributeStore, new HashMap<String, String>(), "attributes");
		attributeStore.setAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID, HOST_EXT_ID);
		openstackHostAdminCapability.getNumberOfCpus();
	}

	/**
	 * Create and/or mock the dependencies of the capability to be tested, as well as the response of those componentes.
	 * 
	 * @throws ProviderNotFoundException
	 */
	private void mockAndCreateCapabilityDependencies() throws URISyntaxException, SecurityException, IllegalArgumentException,
			IllegalAccessException, InstantiationException, EndpointNotFoundException, ApplicationActivationException, ProviderNotFoundException {

		// create and inject resource in capability
		Specification spec = new Specification(Type.HOST, "openstack");
		Endpoint fakeEndpoint = new Endpoint(new URI(RESOURCE_URI));
		UsernamePasswordTenantCredentials credentials = new UsernamePasswordTenantCredentials(USER_ID, PASSWORD, TENANT_ID);

		hostResource = new RootResource(RootResourceDescriptor.create(spec, Arrays.asList(fakeEndpoint), credentials));
		ReflectionTestHelper.injectPrivateField(openstackHostAdminCapability, hostResource, "resource");

		// initialize and inject attributeStore
		attributeStore = new AttributeStore();
		ReflectionTestHelper.injectPrivateField(attributeStore, hostResource, "resource");
		attributeStore.activate();
		attributeStore.setAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID, HOST_EXT_ID);
		attributeStore.setAttribute(OpenstackRootResourceProvider.ZONE_ATTRIBUTE, ZONE);
		ReflectionTestHelper.injectPrivateField(openstackHostAdminCapability, attributeStore, "attributeStore");

		// mock client
		flavor = buildFlavorObject();
		server = buildServerObject(HOST_EXT_ID, HOST_EXT_NAME, flavor);

		mockedNovaApi = PowerMockito.mock(NovaApi.class);
		mockedServerApi = PowerMockito.mock(ServerApi.class);
		PowerMockito.when(mockedNovaApi.getServerApiForZone(Mockito.eq(ZONE))).thenReturn(mockedServerApi);
		PowerMockito.when(mockedServerApi.get(Mockito.eq(HOST_EXT_ID))).thenReturn(server);

		// mock jCloudsClientProvider and inject it in capability.

		mockedJcloudsClientProvider = PowerMockito.mock(IJCloudsNovaClientProvider.class);
		PowerMockito.when(mockedJcloudsClientProvider.getClient(Mockito.eq(hostResource))).thenReturn(mockedNovaApi);
		IClientProviderFactory mockedClientProviderFactory = PowerMockito.mock(IClientProviderFactory.class);
		PowerMockito.when(mockedClientProviderFactory.getClientProvider(Mockito.eq(IJCloudsNovaClientProvider.class))).thenReturn(
				mockedJcloudsClientProvider);
		ReflectionTestHelper.injectPrivateField(openstackHostAdminCapability, mockedClientProviderFactory, "clientProviderFactory");

	}

	/**
	 * Builds a {@link Flavor} instance with the minimal requirements of JClouds client.
	 */
	private Flavor buildFlavorObject() {
		return Flavor.builder().id(FLAVOR_ID).name(FLAVOR_NAME).vcpus(V_CPUS).ram(VM_RAM).disk(VM_DISK).swap(SWAP_SIZE).build();
	}

	/**
	 * Builds a {@link Server} instance with the specificed server id and name, and adds all required information by JClouds to build a server
	 * instance.
	 */
	private Server buildServerObject(String serverId, String serverName, Flavor flavor) {
		return Server.builder().id(serverId).name(serverName).tenantId(TENANT_ID).userId(USER_ID)
				.created(new Date(System.currentTimeMillis())).status(Status.ACTIVE).flavor(flavor).build();
	}
}
