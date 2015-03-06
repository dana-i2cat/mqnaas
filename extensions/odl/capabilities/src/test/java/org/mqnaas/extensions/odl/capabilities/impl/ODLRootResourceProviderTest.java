package org.mqnaas.extensions.odl.capabilities.impl;

/*
 * #%L
 * MQNaaS :: ODL Capabilities
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

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mqnaas.client.cxf.ICXFAPIProvider;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProviderFactory;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.impl.AttributeStore;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.extensions.odl.client.switchnorthbound.ISwitchNorthboundAPI;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.Node;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.Node.NodeType;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnector;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnectorProperties;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnectors;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeProperties;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.Nodes;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.PropertyValue;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.mqnaas.network.api.topology.port.IPortManagement;
import org.mqnaas.network.impl.topology.port.PortManagement;
import org.powermock.api.mockito.PowerMockito;

/**
 * <p>
 * Unitary tests for the {@link ODLRootResourceProvider} implementation
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ODLRootResourceProviderTest {

	private static final String	OFSWITCH_NODE_1_ID	= "00:00:00:00:00:00:00:01";
	private static final String	OFSWITCH_NODE_2_ID	= "00:00:00:00:00:00:00:02";
	private static final String	PE_NODE_ID			= "00:00:00:00:00:00:00:03";

	private static final String	PORT1_EXTERNAL_ID	= "0";
	private static final String	PORT2_EXTERNAL_ID	= "1";
	private static final String	PORT1_EXTERNAL_NAME	= "eth0";
	private static final String	PORT2_EXTERNAL_NAME	= "eth1";

	IRootResourceProvider		odlIRootResourceProvider;

	Nodes						odlNodes;
	NodeConnectors				resource1NodeConnectors;

	Endpoint					fakeOdlEndpoint;

	IResourceManagementListener	mockedRmListener;

	IServiceProvider			mockedServiceProvider;

	IAttributeStore				attributeStoreResource1;
	IAttributeStore				attributeStoreResource2;
	IAttributeStore				attributeStoreResource3;

	IAttributeStore				attributeStorePort1;
	IAttributeStore				attributeStorePort2;

	IPortManagement				resource1PortMgm;
	IPortManagement				resource2PortMgm;
	IPortManagement				resource3PortMgm;

	/**
	 * This method initialize the {@link ODLRootResourceProvider} capability, and create all required structures used on its activation method.
	 */
	@Before
	public void prepareTest() throws SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, URISyntaxException,
			CapabilityNotFoundException, ApplicationActivationException, EndpointNotFoundException, ProviderNotFoundException {

		odlIRootResourceProvider = new ODLRootResourceProvider();

		createFakeOdlResponse();
		mockRequiredFeatures();

		odlIRootResourceProvider.activate();

	}

	/**
	 * This method tests the {@link IRootResourceProvider#getRootResource(String)}, {@link IRootResourceProvider#getRootResources()} and the
	 * {@link IRootResourceProvider#getRootResources(Type, String, String)} implemented in the {@link ODLRootResourceProvider} class. The capability
	 * should contain 2 Openflow switches and 1 "other" resource, representing the Opendaylight devices returned by the mocked client.
	 */
	@Test
	public void capabilityImplementationTest() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, ResourceNotFoundException {

		// test getResources() method
		List<IRootResource> odlResources = odlIRootResourceProvider.getRootResources();

		Assert.assertNotNull("ODLIRootResourceProvider capability should contain three resources.", odlResources);
		Assert.assertEquals("ODLIRootResourceProvider capability should contain three resources.", 3, odlResources.size());

		IRootResource switch1Resource = odlResources.get(0);
		IRootResource switch2Resource = odlResources.get(1);
		IRootResource peResource = odlResources.get(2);

		Assert.assertNotNull("Switch1 resource could not be null.", switch1Resource);
		Assert.assertFalse("Switch1 resource should contain a valid id.", switch1Resource.getId().isEmpty());
		Assert.assertNotNull("Switch1 resource descriptor should have been created.", switch1Resource.getDescriptor());
		Assert.assertNotNull("Switch1 resource specification should have been created.", switch1Resource.getDescriptor()
				.getSpecification());
		Assert.assertEquals("Switch1 resource should be of type " + Type.OF_SWITCH, Type.OF_SWITCH, switch1Resource.getDescriptor()
				.getSpecification().getType());
		Assert.assertNotNull("Switch1 resource should contain ODL network endpoint.", switch1Resource.getDescriptor().getEndpoints());
		Assert.assertEquals("Switch1 resource should contain ODL network endpoint.", 1, switch1Resource.getDescriptor().getEndpoints().size());
		Assert.assertTrue("Switch1 resource should contain ODL network endpoint.",
				switch1Resource.getDescriptor().getEndpoints().contains(fakeOdlEndpoint));
		Assert.assertNotNull("AttributeStore of Switch1 should contain the external port id",
				attributeStoreResource1.getAttribute(AttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("AttributeStore of Switch1 should contain the external port id of the device it represents.", OFSWITCH_NODE_1_ID,
				attributeStoreResource1.getAttribute(AttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("First resource should contain two ports", 2, resource1PortMgm.getPorts().size());
		Assert.assertNotNull("Port1 should contain the mapped external resource id.",
				attributeStorePort1.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertNotNull("Port1 should contain the mapped external resource name",
				attributeStorePort1.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME));
		Assert.assertEquals("Port1 should contain the external port id " + PORT1_EXTERNAL_ID, PORT1_EXTERNAL_ID,
				attributeStorePort1.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("Port1 should contain the external port name " + PORT1_EXTERNAL_NAME, PORT1_EXTERNAL_NAME,
				attributeStorePort1.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME));

		Assert.assertNotNull("Port2 should contain the mapped external resource id.",
				attributeStorePort2.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertNotNull("Port2 should contain the mapped external resource name",
				attributeStorePort2.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME));
		Assert.assertEquals("Port2 should contain the external port id " + PORT2_EXTERNAL_ID, PORT2_EXTERNAL_ID,
				attributeStorePort2.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("Port2 should contain the external port name " + PORT2_EXTERNAL_NAME, PORT2_EXTERNAL_NAME,
				attributeStorePort2.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME));

		Assert.assertNotNull("Switch2 resource could not be null", switch2Resource);
		Assert.assertFalse("Switch2 resource should contain a valid id.", switch2Resource.getId().isEmpty());
		Assert.assertNotNull("Switch2 resource descriptor should have been created.", switch2Resource.getDescriptor());
		Assert.assertEquals("Switch2 resource should be of type " + Type.OF_SWITCH, Type.OF_SWITCH, switch2Resource.getDescriptor()
				.getSpecification().getType());
		Assert.assertNotNull("Switch2 resource should contain ODL network endpoint.", switch2Resource.getDescriptor().getEndpoints());
		Assert.assertEquals("Switch2 resource should contain ODL network endpoint.", 1, switch2Resource.getDescriptor().getEndpoints().size());
		Assert.assertTrue("Switch2 resource should contain ODL network endpoint.",
				switch2Resource.getDescriptor().getEndpoints().contains(fakeOdlEndpoint));
		Assert.assertNotNull("AttributeStore of Switch2 should contain the external port id",
				attributeStoreResource2.getAttribute(AttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("AttributeStore of Switch2 should contain the external port id of the device it represents.", OFSWITCH_NODE_2_ID,
				attributeStoreResource2.getAttribute(AttributeStore.RESOURCE_EXTERNAL_ID));

		Assert.assertNotNull("EP resource could not be null.", peResource);
		Assert.assertFalse("EP resource should contain a valid id.", peResource.getId().isEmpty());
		Assert.assertNotNull("EP resource descriptor should have been created.", peResource.getDescriptor());
		Assert.assertEquals("EP resource should be of type " + Type.OTHER, Type.OTHER, peResource.getDescriptor()
				.getSpecification().getType());
		Assert.assertNotNull("EP resource should contain ODL network endpoint.", peResource.getDescriptor().getEndpoints());
		Assert.assertEquals("EP resource should contain ODL network endpoint.", 1, peResource.getDescriptor().getEndpoints().size());
		Assert.assertTrue("EP resource should contain ODL network endpoint.",
				peResource.getDescriptor().getEndpoints().contains(fakeOdlEndpoint));
		Assert.assertNotNull("AttributeStore of EP should contain the external port id",
				attributeStoreResource3.getAttribute(AttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("AttributeStore of EP should contain the external port id of the device it represents.", PE_NODE_ID,
				attributeStoreResource3.getAttribute(AttributeStore.RESOURCE_EXTERNAL_ID));

		// test getRootResources(type,model,version)
		List<IRootResource> switches = odlIRootResourceProvider.getRootResources(Type.OF_SWITCH, null, null);
		Assert.assertNotNull("OdlRootResourceprovider should contain two openflow switches.", switches);
		Assert.assertEquals("OdlRootResourceprovider should contain two openflow switches.", 2, switches.size());
		Assert.assertTrue("OdlRootResourceProvider should contain switch1.", switches.contains(switch1Resource));
		Assert.assertTrue("OdlRootResourceProvider should contain switch2.", switches.contains(switch2Resource));

		List<IRootResource> pes = odlIRootResourceProvider.getRootResources(Type.OTHER, null, null);
		Assert.assertNotNull("OdlRootResourceprovider should contain one EP resource.", pes);
		Assert.assertEquals("OdlRootResourceprovider should contain tone EP resource.", 1, pes.size());
		Assert.assertTrue("OdlRootResourceProvider should contain EP resource.", pes.contains(peResource));

		// test getRootResource(id)
		IRootResource resource = odlIRootResourceProvider.getRootResource(switch1Resource.getId());
		Assert.assertEquals("OdlRootResourceProvier capability did not retreive the requested resource.", switch1Resource, resource);
		resource = odlIRootResourceProvider.getRootResource(switch2Resource.getId());
		Assert.assertEquals("OdlRootResourceProvier capability did not retreive the requested resource.", switch2Resource, resource);
		resource = odlIRootResourceProvider.getRootResource(peResource.getId());
		Assert.assertEquals("OdlRootResourceProvier capability did not retreive the requested resource.", peResource, resource);

	}

	/**
	 * This method tests the {@link ODLRootResourceProvider#isSupporting(IRootResource)} method. It checks that this capability implementation would
	 * only be bound to ODL networks.
	 *
	 */
	@Test
	public void isSupportingTest() throws InstantiationException, IllegalAccessException {
		IRootResource rootResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK, "odl")));
		Assert.assertTrue(ODLRootResourceProvider.isSupporting(rootResource));

		rootResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));
		Assert.assertFalse(ODLRootResourceProvider.isSupporting(rootResource));

	}

	/**
	 * Create the {@link Nodes} object that the OpenDaylight instance would answer, containing 2 openflow switches and one PE.
	 */
	private void createFakeOdlResponse() {

		odlNodes = new Nodes();

		NodeProperties switch1NodeProperties = generateNodeProperties(OFSWITCH_NODE_1_ID, NodeType.OF);
		NodeProperties switch2NodeProperties = generateNodeProperties(OFSWITCH_NODE_2_ID, NodeType.OF);
		NodeProperties peNodeProperties = generateNodeProperties(PE_NODE_ID, NodeType.PE);

		odlNodes.setNodeProperties(Arrays.asList(switch1NodeProperties, switch2NodeProperties, peNodeProperties));

		resource1NodeConnectors = new NodeConnectors();

		NodeConnectorProperties port1Properties = generateNodeConnectorProperties(PORT1_EXTERNAL_ID, PORT1_EXTERNAL_NAME);
		NodeConnectorProperties port2Properties = generateNodeConnectorProperties(PORT2_EXTERNAL_ID, PORT2_EXTERNAL_NAME);

		resource1NodeConnectors.setNodeConnectorProperties(Arrays.asList(port1Properties, port2Properties));

	}

	private NodeConnectorProperties generateNodeConnectorProperties(String ncId, String ncName) {

		NodeConnectorProperties ncProperties = new NodeConnectorProperties();
		NodeConnector nc = new NodeConnector();
		nc.setNodeConnectorID(ncId);

		ncProperties.setNodeConnector(nc);
		Map<String, PropertyValue> ncPropertiesMap = new HashMap<String, PropertyValue>();
		ncPropertiesMap.put("name", new PropertyValue(ncName, null));
		ncProperties.setProperties(ncPropertiesMap);

		return ncProperties;

	}

	/**
	 * Creates an instance of the {@link NodeProperties} object, with the specified id and type.
	 * 
	 * @param nodeId
	 *            Id of the node.
	 * @param nodeType
	 *            Type of the node.
	 * @return {@link NodeProperties} containing a node with id <code>nodeId</code> and type <code>nodeType</node>
	 */
	private NodeProperties generateNodeProperties(String nodeId, NodeType nodeType) {

		Node node = new Node();
		node.setNodeID(nodeId);
		node.setNodeType(nodeType);

		NodeProperties nodeProperties = new NodeProperties();
		nodeProperties.setNode(node);

		return nodeProperties;
	}

	/**
	 * Mock all capabilities and resources used by the {@link ODLRootResourceProvider} implementation.
	 * 
	 * @throws ApplicationActivationException
	 */
	private void mockRequiredFeatures() throws SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException,
			URISyntaxException, CapabilityNotFoundException, EndpointNotFoundException, ProviderNotFoundException, ApplicationActivationException {

		// mock client
		ISwitchNorthboundAPI mockedOdlClient = PowerMockito.mock(ISwitchNorthboundAPI.class);
		PowerMockito.when(mockedOdlClient.getNodes(Mockito.eq("default"))).thenReturn(odlNodes);
		PowerMockito.when(
				mockedOdlClient.getNodeConnectors(Mockito.eq("default"), Mockito.eq(NodeType.OF.toString()), Mockito.eq(OFSWITCH_NODE_1_ID)))
				.thenReturn(resource1NodeConnectors);
		PowerMockito.when(
				mockedOdlClient.getNodeConnectors(Mockito.eq("default"), Mockito.eq(NodeType.OF.toString()), Mockito.eq(OFSWITCH_NODE_2_ID)))
				.thenReturn(resource1NodeConnectors);
		PowerMockito.when(
				mockedOdlClient.getNodeConnectors(Mockito.eq("default"), Mockito.eq(NodeType.PE.toString()), Mockito.eq(PE_NODE_ID))).thenReturn(
				resource1NodeConnectors);

		// mock and inject apiclientprovider

		ICXFAPIProvider mockedCxfApiProvider = PowerMockito.mock(ICXFAPIProvider.class);
		PowerMockito.when(mockedCxfApiProvider.getAPIClient(Mockito.any(IResource.class), Mockito.eq(ISwitchNorthboundAPI.class))).thenReturn(
				mockedOdlClient);

		IAPIClientProviderFactory mockedApiProviderFactory = PowerMockito.mock(IAPIClientProviderFactory.class);
		PowerMockito.when(mockedApiProviderFactory.getAPIProvider(Mockito.eq(ICXFAPIProvider.class))).thenReturn(mockedCxfApiProvider);
		ReflectionTestHelper.injectPrivateField(odlIRootResourceProvider, mockedApiProviderFactory, "apiProviderFactory");

		// inject fake resource in capability
		fakeOdlEndpoint = new Endpoint(new URI("http://www.myfakeodl.com/"));
		IRootResource fakeOdlResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK, "odl"),
				Arrays.asList(fakeOdlEndpoint)));
		ReflectionTestHelper.injectPrivateField(odlIRootResourceProvider, fakeOdlResource, "resource");

		// mock and inject rmListener
		mockedRmListener = PowerMockito.mock(IResourceManagementListener.class);
		ReflectionTestHelper.injectPrivateField(odlIRootResourceProvider, mockedRmListener, "rmListener");

		// mock and inject serviceProvider
		attributeStoreResource1 = new AttributeStore();
		attributeStoreResource2 = new AttributeStore();
		attributeStoreResource3 = new AttributeStore();
		attributeStorePort1 = new AttributeStore();
		attributeStorePort2 = new AttributeStore();

		ReflectionTestHelper.injectPrivateField(attributeStoreResource1, new ConcurrentHashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStoreResource2, new ConcurrentHashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStoreResource3, new ConcurrentHashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStorePort1, new ConcurrentHashMap<String, String>(), "attributes");
		ReflectionTestHelper.injectPrivateField(attributeStorePort2, new ConcurrentHashMap<String, String>(), "attributes");

		mockedServiceProvider = PowerMockito.mock(IServiceProvider.class);
		PowerMockito.when(mockedServiceProvider.getCapability(Mockito.any(IRootResource.class), Mockito.eq(IAttributeStore.class)))
				.thenReturn(attributeStoreResource1).thenReturn(attributeStoreResource2).thenReturn(attributeStoreResource3);
		PowerMockito
				.when(mockedServiceProvider.getCapability(
						AdditionalMatchers.and(Matchers.isA(IResource.class), AdditionalMatchers.not(Matchers.isA(IRootResource.class))),
						Mockito.eq(IAttributeStore.class)))
				.thenReturn(attributeStorePort1).thenReturn(attributeStorePort2);

		resource1PortMgm = new PortManagement();
		resource1PortMgm.activate();
		resource2PortMgm = new PortManagement();
		resource2PortMgm.activate();
		resource3PortMgm = new PortManagement();
		resource3PortMgm.activate();

		PowerMockito.when(mockedServiceProvider.getCapability(Mockito.any(IRootResource.class), Mockito.eq(IPortManagement.class)))
				.thenReturn(resource1PortMgm).thenReturn(resource2PortMgm).thenReturn(resource3PortMgm);
		ReflectionTestHelper.injectPrivateField(odlIRootResourceProvider, mockedServiceProvider, "serviceProvider");

	}
}
