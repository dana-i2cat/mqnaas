package org.mqnaas.network.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.Cube;
import org.mqnaas.core.api.slicing.CubesList;
import org.mqnaas.core.api.slicing.ISliceAdministration;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.core.api.slicing.IUnitAdministration;
import org.mqnaas.core.api.slicing.IUnitManagement;
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.api.slicing.SlicingException;
import org.mqnaas.core.impl.AttributeStore;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.core.impl.slicing.Slice;
import org.mqnaas.core.impl.slicing.SliceAdministration;
import org.mqnaas.core.impl.slicing.SliceProvider;
import org.mqnaas.core.impl.slicing.UnitAdministration;
import org.mqnaas.core.impl.slicing.UnitManagment;
import org.mqnaas.core.impl.slicing.UnitResource;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.mqnaas.network.api.exceptions.NetworkCreationException;
import org.mqnaas.network.api.request.IRequestAdministration;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.api.request.IRequestManagement;
import org.mqnaas.network.api.request.IRequestResourceManagement;
import org.mqnaas.network.api.request.IRequestResourceMapping;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.topology.link.ILinkAdministration;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;
import org.mqnaas.network.api.topology.port.IPortManagement;
import org.mqnaas.network.impl.request.Request;
import org.mqnaas.network.impl.request.RequestAdministration;
import org.mqnaas.network.impl.request.RequestManagement;
import org.mqnaas.network.impl.request.RequestResource;
import org.mqnaas.network.impl.request.RequestResourceManagement;
import org.mqnaas.network.impl.request.RequestResourceMapping;
import org.mqnaas.network.impl.topology.link.LinkAdministration;
import org.mqnaas.network.impl.topology.link.LinkManagement;
import org.mqnaas.network.impl.topology.link.LinkResource;
import org.mqnaas.network.impl.topology.port.NetworkPortManagement;
import org.mqnaas.network.impl.topology.port.PortManagement;
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

	@Test
	public void createSliteTest() throws InstantiationException, IllegalAccessException, URISyntaxException, ApplicationActivationException,
			CapabilityNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException,
			SlicingException {

		final String portUnit = "port";

		// create network
		IRootResource network = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));

		// create physical of_switch
		Endpoint endpoint = new Endpoint(new URI("http://www.myfakeresource.com/"));
		IRootResource physicalResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.OF_SWITCH), Arrays.asList(endpoint)));

		// create physical slice provider capability
		ISliceProvider phySliceProvider = new SliceProvider();
		IResourceManagementListener rmListener = PowerMockito.mock(IResourceManagementListener.class);
		ReflectionTestHelper.injectPrivateField(phySliceProvider, rmListener, "resourceManagementListener");
		ReflectionTestHelper.injectPrivateField(phySliceProvider, physicalResource, "resource");
		phySliceProvider.activate();
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(physicalResource), Mockito.eq(ISliceProvider.class))).thenReturn(
				phySliceProvider);

		// create virtual of_switch
		IRootResource virtualResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.OF_SWITCH), Arrays.asList(endpoint)));

		// create virtual slice provider capability
		ISliceProvider virtSliceProvider = new SliceProvider();
		ReflectionTestHelper.injectPrivateField(virtSliceProvider, rmListener, "resourceManagementListener");
		ReflectionTestHelper.injectPrivateField(virtSliceProvider, virtualResource, "resource");
		virtSliceProvider.activate();
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(virtualResource), Mockito.eq(ISliceProvider.class))).thenReturn(
				virtSliceProvider);

		// create physical slice units
		IUnitManagement phySliceUnitMgm = new UnitManagment();
		ReflectionTestHelper.injectPrivateField(phySliceUnitMgm, phySliceProvider.getSlice(), "resource");
		phySliceUnitMgm.activate();
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(phySliceProvider.getSlice()), Mockito.eq(IUnitManagement.class))).thenReturn(
				phySliceUnitMgm);

		IResource phyPortUnit = phySliceUnitMgm.createUnit(portUnit);

		// create physical port unit administration capability with ports [0-3]
		IUnitAdministration phyPortUnitAdmin = new UnitAdministration();
		ReflectionTestHelper.injectPrivateField(phyPortUnitAdmin, phyPortUnit, "resource");
		phyPortUnitAdmin.activate();
		phyPortUnitAdmin.setRange(new Range(0, 3));
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(phyPortUnit), Mockito.eq(IUnitAdministration.class))).thenReturn(
				phyPortUnitAdmin);

		// initialize slice cubes with cube [0,3]
		ISliceAdministration phySliceAdmin = new SliceAdministration();
		ReflectionTestHelper.injectPrivateField(phySliceAdmin, phySliceProvider.getSlice(), "resource");
		ReflectionTestHelper.injectPrivateField(phySliceAdmin, serviceProvider, "serviceProvider");
		phySliceAdmin.activate();
		Range[] phyRanges = { new Range(0, 3) };
		Cube cube = new Cube(phyRanges);
		phySliceAdmin.setCubes(new CubesList(Arrays.asList(cube)));
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(phySliceProvider.getSlice()), Mockito.eq(ISliceAdministration.class))).thenReturn(
				phySliceAdmin);
		Slice phySlice = new Slice(phySliceProvider.getSlice(), serviceProvider);
		Assert.assertEquals("XXXX", phySlice.toMatrix());

		// create virtual slice units
		IUnitManagement virtSliceUnitMgm = new UnitManagment();
		ReflectionTestHelper.injectPrivateField(virtSliceUnitMgm, virtSliceProvider.getSlice(), "resource");
		virtSliceUnitMgm.activate();
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(virtSliceProvider.getSlice()), Mockito.eq(IUnitManagement.class))).thenReturn(
				virtSliceUnitMgm);

		IResource virtPortUnit = virtSliceUnitMgm.createUnit(portUnit);

		// create virtual port unit administration capability with range [0-3]
		IUnitAdministration virtPortUnitAdmin = new UnitAdministration();
		ReflectionTestHelper.injectPrivateField(virtPortUnitAdmin, virtPortUnit, "resource");
		virtPortUnitAdmin.activate();
		virtPortUnitAdmin.setRange(new Range(0, 3));
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(virtPortUnit), Mockito.eq(IUnitAdministration.class))).thenReturn(
				virtPortUnitAdmin);

		// initialize slice cubes with cube [2,3]
		ISliceAdministration virtSliceAdmin = new SliceAdministration();
		ReflectionTestHelper.injectPrivateField(virtSliceAdmin, virtSliceProvider.getSlice(), "resource");
		ReflectionTestHelper.injectPrivateField(virtSliceAdmin, serviceProvider, "serviceProvider");
		virtSliceAdmin.activate();
		Range[] virtRanges = { new Range(2, 3) };
		Cube virtCube = new Cube(virtRanges);
		virtSliceAdmin.setCubes(new CubesList(Arrays.asList(virtCube)));
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(virtSliceProvider.getSlice()), Mockito.eq(ISliceAdministration.class)))
				.thenReturn(virtSliceAdmin);
		Slice virtslice = new Slice(virtSliceProvider.getSlice(), serviceProvider);
		Assert.assertEquals("OOXX", virtslice.toMatrix());

		// create new slices resource capabilities
		IRootResource newResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.OF_SWITCH, "virtual"),
				Arrays.asList(endpoint)));
		ISliceProvider slicedResourceSliceProvider = new SliceProvider();
		ReflectionTestHelper.injectPrivateField(slicedResourceSliceProvider, rmListener, "resourceManagementListener");
		ReflectionTestHelper.injectPrivateField(slicedResourceSliceProvider, newResource, "resource");
		slicedResourceSliceProvider.activate();
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(newResource), Mockito.eq(ISliceProvider.class))).thenReturn(
				slicedResourceSliceProvider);

		IUnitManagement slicedResourceUnitMgm = new UnitManagment();
		ReflectionTestHelper.injectPrivateField(slicedResourceUnitMgm, slicedResourceSliceProvider.getSlice(), "resource");
		slicedResourceUnitMgm.activate();
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(slicedResourceSliceProvider.getSlice()), Mockito.eq(IUnitManagement.class)))
				.thenReturn(slicedResourceUnitMgm);

		IUnitAdministration slicedResourceUnitAdmin = new UnitAdministration();
		ISliceAdministration slicedResourceSliceAdmin = new SliceAdministration();
		ReflectionTestHelper.injectPrivateField(slicedResourceSliceAdmin, slicedResourceSliceProvider.getSlice(), "resource");
		ReflectionTestHelper.injectPrivateField(slicedResourceSliceAdmin, serviceProvider, "serviceProvider");
		slicedResourceSliceAdmin.activate();
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(slicedResourceSliceProvider.getSlice()), Mockito.eq(ISliceAdministration.class)))
				.thenReturn(slicedResourceSliceAdmin);
		PowerMockito.when(
				serviceProvider.getCapability(
						AdditionalMatchers.and(AdditionalMatchers.not(Mockito.eq(phyPortUnit)), (AdditionalMatchers.not(Mockito.eq(virtPortUnit)))),
						Mockito.eq(IUnitAdministration.class))).thenReturn(slicedResourceUnitAdmin);

		// create mocked slicing capability
		ISlicingCapability mockedSlicingCapab = PowerMockito.mock(ISlicingCapability.class);
		PowerMockito.when(mockedSlicingCapab.createSlice(Mockito.any(IResource.class))).thenReturn(newResource);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(physicalResource), Mockito.eq(ISlicingCapability.class))).thenReturn(
				mockedSlicingCapab);

		PowerMockito.when(serviceProvider.getCapabilityInstance(Mockito.any(IResource.class), Mockito.any(Class.class))).thenReturn(null);
		ReflectionTestHelper.injectPrivateField(networkManagementCapab, rmListener, "resourceManagementListener");

		// call method by reflection
		Method method = networkManagementCapab.getClass().getDeclaredMethod("createSlice", Network.class, NetworkSubResource.class,
				NetworkSubResource.class);
		method.setAccessible(true);

		method.invoke(networkManagementCapab, new Network(network, serviceProvider), new NetworkSubResource(physicalResource, serviceProvider),
				new NetworkSubResource(virtualResource, serviceProvider));

		// assert physical slice contains updated data
		Assert.assertEquals("XXOO", phySlice.toMatrix());

		// assert new sliced resource contains proper slice information
		Assert.assertFalse("Created slice should contain one unit.", slicedResourceUnitMgm.getUnits().isEmpty());
		Assert.assertEquals("Created slice should contain one unit.", 1, slicedResourceUnitMgm.getUnits().size());
		Assert.assertEquals("Created slice should contain the port unit.", portUnit,
				((UnitResource) slicedResourceUnitMgm.getUnits().get(0)).getName());

		Assert.assertTrue("Create slice should contain same units ranges as original one.", 0 == slicedResourceUnitAdmin.getRange().getLowerBound());
		Assert.assertTrue("Create slice should contain same units ranges as original one.", 3 == slicedResourceUnitAdmin.getRange().getUpperBound());

		Slice slicedResourceSlice = new Slice(slicedResourceSliceProvider.getSlice(), serviceProvider);
		Assert.assertEquals("Create slice should contain same slice cubes as the virtual one.", "OOXX", slicedResourceSlice.toMatrix());

	}

	@Test
	public void createResourcePortsTest() throws URISyntaxException, InstantiationException, IllegalAccessException, CapabilityNotFoundException,
			ApplicationActivationException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

		final String portExternalId = "eth0";

		// create slicedResource
		Endpoint endpoint = new Endpoint(new URI("http://www.myfakeresource.com/"));
		IRootResource slicedResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.OF_SWITCH), Arrays.asList(endpoint)));

		// create ports
		PortResource phyPort = new PortResource();
		PortResource virtPort = new PortResource();

		// map ports in request
		reqMappingCapab.defineMapping(virtPort, phyPort);

		// create portManagement capability
		IPortManagement portManagement = new PortManagement();
		ReflectionTestHelper.injectPrivateField(portManagement, new CopyOnWriteArrayList<PortResource>(), "ports");

		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(slicedResource), Mockito.eq(IPortManagement.class))).thenReturn(portManagement);

		// create mocked IResourceManagementListener
		IResourceManagementListener rmListener = PowerMockito.mock(IResourceManagementListener.class);
		ReflectionTestHelper.injectPrivateField(networkManagementCapab, rmListener, "resourceManagementListener");
		ReflectionTestHelper.injectPrivateField(networkManagementCapab, serviceProvider, "serviceProvider");

		// mock attribute store capabilities
		IAttributeStore phyPortAttributeStore = new AttributeStore();
		ReflectionTestHelper.injectPrivateField(phyPortAttributeStore, phyPort, "resource");
		phyPortAttributeStore.activate();
		IAttributeStore slicedResourcePortAttributeStore = new AttributeStore();
		ReflectionTestHelper.injectPrivateField(slicedResourcePortAttributeStore, new ConcurrentHashMap<String, String>(), "attributes");
		phyPortAttributeStore.setAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE, portExternalId);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(phyPort), Mockito.eq(IAttributeStore.class))).thenReturn(
				phyPortAttributeStore);
		PowerMockito.when(serviceProvider.getCapability(AdditionalMatchers.not(Mockito.eq(phyPort)), Mockito.eq(IAttributeStore.class))).thenReturn(
				slicedResourcePortAttributeStore);

		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(requestResource), Mockito.eq(IRequestResourceMapping.class))).thenReturn(
				reqMappingCapab);

		Assert.assertTrue("Slices resource should contain no ports on startup.", portManagement.getPorts().isEmpty());

		// call method by reflection
		Method method = networkManagementCapab.getClass().getDeclaredMethod("createResourcePorts", List.class, IResource.class,
				Request.class);
		method.setAccessible(true);

		method.invoke(networkManagementCapab, Arrays.asList(virtPort), slicedResource, request);

		// assert sliced resource has one port with mapped id
		Assert.assertFalse("Slices resource should contain one port.", portManagement.getPorts().isEmpty());
		Assert.assertEquals("Slices resource should contain one port.", 1, portManagement.getPorts().size());
		Assert.assertEquals(portExternalId, slicedResourcePortAttributeStore.getAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE));
	}

	@Test
	public void delegateToSubnetworkTest() throws InstantiationException, IllegalAccessException, CapabilityNotFoundException,
			ApplicationActivationException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException,
			URISyntaxException, NetworkCreationException {

		// created fake network that "would be created" by the subnetwork
		IRootResource createdSubnet = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK, "virtual")));

		// create networks
		IRootResource physicalSubNetworkResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));
		IRootResource virtualSubnetworkResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK, "virtual")));
		Network virtualSubnetwork = new Network(virtualSubnetworkResource, serviceProvider);

		// map networks
		reqMappingCapab.defineMapping(virtualSubnetworkResource, physicalSubNetworkResource);

		// create physical ofSwitch
		Endpoint endpoint = new Endpoint(new URI("http://www.myfakeresource.com/"));
		IRootResource phyOfSwitch = new RootResource(RootResourceDescriptor.create(new Specification(Type.OF_SWITCH), Arrays.asList(endpoint)));

		// create physical subnetwork request capability
		IRequestManagement phySubnetReqMgm = Mockito.spy(new RequestManagement());
		phySubnetReqMgm.activate();
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(physicalSubNetworkResource), Mockito.eq(IRequestManagement.class))).thenReturn(
				phySubnetReqMgm);

		// create created virtual subnetwork requestResourceManagement
		IRequestResourceManagement phySubnetReqResourceMgm = Mockito.spy(new RequestResourceManagement());
		phySubnetReqResourceMgm.activate();

		// create virtual subnetwork requestResourceManagement
		IRequestResourceManagement virtSubnetReqResourceMgm = new RequestResourceManagement();
		virtSubnetReqResourceMgm.activate();
		IResource virtOfSwich = virtSubnetReqResourceMgm.createResource(Type.OF_SWITCH);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(virtualSubnetworkResource), Mockito.eq(IRequestResourceManagement.class)))
				.thenReturn(virtSubnetReqResourceMgm);

		// define mapping between virtual and physical ofswitch
		reqMappingCapab.defineMapping(virtOfSwich, phyOfSwitch);

		// create virtual subnetwork request IRequestResourceMapping capability
		IRequestResourceMapping virtSubnetRequestresourceMapping = new RequestResourceMapping();
		virtSubnetRequestresourceMapping.activate();
		PowerMockito.when(
				serviceProvider.getCapability(AdditionalMatchers.not(Mockito.eq(request.getRequestResource())),
						Mockito.eq(IRequestResourceMapping.class))).thenReturn(virtSubnetRequestresourceMapping);

		// create physical and virtual subnetwork periods
		Period period = new Period(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()));
		IRequestAdministration reqAdministration = new RequestAdministration();
		reqAdministration.setPeriod(period);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(requestResource), Mockito.eq(IRequestAdministration.class))).thenReturn(
				reqAdministration);
		IRequestAdministration subnetReqAdmin = Mockito.spy(new RequestAdministration());
		PowerMockito.when(
				serviceProvider.getCapability(AdditionalMatchers.not(Mockito.eq(requestResource)), Mockito.eq(IRequestAdministration.class)))
				.thenReturn(subnetReqAdmin);

		// mock IRequestBasedNetworkManagement from subnetwork
		IRequestBasedNetworkManagement subneReqBasedNetMgm = PowerMockito.mock(IRequestBasedNetworkManagement.class);
		PowerMockito.when(subneReqBasedNetMgm.createNetwork(AdditionalMatchers.not(Mockito.eq(request.getRequestResource())))).thenReturn(
				createdSubnet);
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(physicalSubNetworkResource), Mockito.eq(IRequestBasedNetworkManagement.class)))
				.thenReturn(subneReqBasedNetMgm);

		// mock service provider to return desired capabilities
		PowerMockito.when(serviceProvider.getCapability(Mockito.eq(requestResource), Mockito.eq(IRequestResourceMapping.class))).thenReturn(
				reqMappingCapab);
		ReflectionTestHelper.injectPrivateField(networkManagementCapab, serviceProvider, "serviceProvider");
		PowerMockito.when(
				serviceProvider.getCapability(AdditionalMatchers.and(AdditionalMatchers.not(Mockito.eq(virtualSubnetworkResource)),
						AdditionalMatchers.not(Mockito.eq(request.getRequestResource()))),
						Mockito.eq(IRequestResourceManagement.class))).thenReturn(phySubnetReqResourceMgm);

		// call method by reflection
		Method method = networkManagementCapab.getClass().getDeclaredMethod("delegateToSubnetwork", Request.class, Network.class);
		method.setAccessible(true);

		Network network = (Network) method.invoke(networkManagementCapab, request, virtualSubnetwork);

		// assert returned network is the expected one
		Assert.assertEquals("Created network should contain the virtual network created by the physical subnetwork", createdSubnet,
				network.getNetworkResource());

		// assert only one request has been created for subnetwork
		Mockito.verify(phySubnetReqMgm, Mockito.times(1)).createRequest();

		// assert one resource was created in the request, with the correct resource type
		Mockito.verify(phySubnetReqResourceMgm, Mockito.times(1)).createResource(Type.OF_SWITCH);

		// assert period was set in request
		Mockito.verify(subnetReqAdmin, Mockito.times(1)).setPeriod(period);

	}
}
