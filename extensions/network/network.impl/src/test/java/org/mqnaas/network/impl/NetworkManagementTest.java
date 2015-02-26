package org.mqnaas.network.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.mqnaas.network.api.request.IRequestResourceMapping;
import org.mqnaas.network.api.topology.link.ILinkAdministration;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;
import org.mqnaas.network.impl.request.Request;
import org.mqnaas.network.impl.request.RequestResource;
import org.mqnaas.network.impl.request.RequestResourceMapping;
import org.mqnaas.network.impl.topology.link.LinkAdministration;
import org.mqnaas.network.impl.topology.link.LinkManagement;
import org.mqnaas.network.impl.topology.link.LinkResource;
import org.mqnaas.network.impl.topology.port.NetworkPortManagement;
import org.mqnaas.network.impl.topology.port.PortResource;
import org.powermock.api.mockito.PowerMockito;

public class NetworkManagementTest {

	private NetworkManagement		networkManagementCapab;
	private IRootResource			virtualNetwork;
	private Request					request;
	private RequestResource			requestResource;

	private IServiceProvider		serviceProvider;

	private IRequestResourceMapping	reqMappingCapab;

	@Before
	public void prepareTest() throws InstantiationException, IllegalAccessException, ApplicationActivationException {

		virtualNetwork = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK, "virtual")));
		networkManagementCapab = new NetworkManagement();
		reqMappingCapab = new RequestResourceMapping();

		serviceProvider = PowerMockito.mock(IServiceProvider.class);

		requestResource = new RequestResource();
		request = new Request(requestResource, serviceProvider);

		// inject service provider in networkManagement capability
		ReflectionTestHelper.injectPrivateField(networkManagementCapab, serviceProvider, "serviceProvider");

		// activate capabilities
		reqMappingCapab.activate();

	}

	@Test
	public void createNetworkLinksTest() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			SecurityException, NoSuchMethodException, CapabilityNotFoundException, ApplicationActivationException {

		// create ports
		PortResource phyPort1 = new PortResource();
		PortResource phyPort2 = new PortResource();

		PortResource virtPort1 = new PortResource();
		PortResource virtPort2 = new PortResource();

		// map ports in request
		reqMappingCapab.defineMapping(virtPort1, phyPort1);
		reqMappingCapab.defineMapping(virtPort2, phyPort2);

		// create links capabilities
		ILinkManagement netLinkManagementCapab = new LinkManagement();
		ILinkManagement reqLinkManagementCapab = new LinkManagement();
		ILinkAdministration reqLinkAdministration = new LinkAdministration();
		ILinkAdministration netLinkAdministration = new LinkAdministration();

		netLinkManagementCapab.activate();
		reqLinkManagementCapab.activate();

		// create link in request
		reqLinkManagementCapab.createLink();
		// set req link ports
		reqLinkAdministration.setSrcPort(virtPort1);
		reqLinkAdministration.setDestPort(virtPort2);

		// mock service provider responses
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(virtualNetwork), Mockito.eq(ILinkManagement.class))).thenReturn(
				netLinkManagementCapab);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(requestResource), Mockito.eq(ILinkManagement.class))).thenReturn(
				reqLinkManagementCapab);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(requestResource), Mockito.eq(IRequestResourceMapping.class))).thenReturn(
				reqMappingCapab);
		PowerMockito.when(serviceProvider.getCapability(Mockito.any(LinkResource.class), Mockito.eq(ILinkAdministration.class))).thenReturn(
				reqLinkAdministration).thenReturn(reqLinkAdministration).thenReturn(netLinkAdministration);

		// assert network has no links
		Assert.assertTrue(netLinkManagementCapab.getLinks().isEmpty());

		// call method by reflection
		Method method = networkManagementCapab.getClass().getDeclaredMethod("createNetworkLinks", IRootResource.class, Request.class);
		method.setAccessible(true);

		method.invoke(networkManagementCapab, virtualNetwork, request);

		// assert network has 1 link with corresponding ports
		Assert.assertFalse("Network should contain a configured link.", netLinkManagementCapab.getLinks().isEmpty());
		Assert.assertEquals("Network should contain a configured link.", 1, netLinkManagementCapab.getLinks().size());

		Assert.assertNotNull("Virtual link should contain a source port", netLinkAdministration.getSrcPort());
		Assert.assertNotNull("Virtual link should contain a destination port", netLinkAdministration.getDestPort());

		Assert.assertEquals("Virtual link should contain phyport1 as source port", phyPort1, netLinkAdministration.getSrcPort());
		Assert.assertEquals("Virtual link should contain phyport2 as destination port", phyPort2, netLinkAdministration.getDestPort());

	}

	@Test
	public void defineNetworkPortsTest() throws ApplicationActivationException, CapabilityNotFoundException, SecurityException,
			NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		// create physical port
		PortResource phyPort = new PortResource();
		PortResource virtPort = new PortResource();

		// create request network ports
		INetworkPortManagement reqPortMgmCapab = new NetworkPortManagement();
		reqPortMgmCapab.activate();
		reqPortMgmCapab.addPort(virtPort);

		// map ports in request
		reqMappingCapab.defineMapping(virtPort, phyPort);

		// create networkPortManagement capability
		INetworkPortManagement netPortMgmCapab = new NetworkPortManagement();
		netPortMgmCapab.activate();

		// mock service provider to return desired capabilities
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(requestResource), Mockito.eq(INetworkPortManagement.class))).thenReturn(
				reqPortMgmCapab);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(virtualNetwork), Mockito.eq(INetworkPortManagement.class))).thenReturn(
				netPortMgmCapab);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(requestResource), Mockito.eq(IRequestResourceMapping.class))).thenReturn(
				reqMappingCapab);

		// assert network has no ports
		Assert.assertTrue(netPortMgmCapab.getPorts().isEmpty());

		// call method by reflection
		Method method = networkManagementCapab.getClass().getDeclaredMethod("defineNetworkPorts", IRootResource.class, Request.class);
		method.setAccessible(true);

		method.invoke(networkManagementCapab, virtualNetwork, request);

		// Assert network has desired port
		Assert.assertFalse("Virtual network should contain one network port.", netPortMgmCapab.getPorts().isEmpty());
		Assert.assertEquals("Virtual network should contain one network port.", 1, netPortMgmCapab.getPorts().size());

		Assert.assertEquals("Network port should contain physical port as network port.", phyPort, netPortMgmCapab.getPorts().get(0));

	}
}
