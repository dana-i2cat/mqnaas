package org.mqnaas.extensions.network.itests;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import net.i2cat.dana.nitos.client.model.LeaseResourcesResponse;
import net.i2cat.dana.nitos.client.model.Resource;
import net.i2cat.dana.nitos.client.model.ResourceResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.api.slicing.Cube;
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.impl.slicing.Slice;
import org.mqnaas.core.impl.slicing.Unit;
import org.mqnaas.network.api.exceptions.NetworkCreationException;
import org.mqnaas.network.api.exceptions.NetworkReleaseException;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.api.request.IRequestManagement;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;
import org.mqnaas.network.impl.Link;
import org.mqnaas.network.impl.Network;
import org.mqnaas.network.impl.NetworkManagement;
import org.mqnaas.network.impl.NetworkSubResource;
import org.mqnaas.network.impl.PortResourceWrapper;
import org.mqnaas.network.impl.request.Request;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class NetworkManagementTest {

	private static final Logger	log				= LoggerFactory.getLogger(NetworkManagementTest.class);

	private static final String	TSON_PORT_1		= "tson-port-1";
	private static final String	TSON_PORT_2		= "tson-port-2";
	private static final String	TSON_PORT_3		= "tson-port-3";

	private Network				networkResource;
	private NetworkSubResource	tsonResource;
	private Network				nitosResource;

	private NetworkSubResource	ofSwitchResource1;
	private NetworkSubResource	ofSwitchResource2;

	@Rule
	public WireMockRule			wireMockRule	= new WireMockRule(8080);

	@Inject
	IServiceProvider			serviceProvider;

	@Inject
	IRootResourceAdministration	rootResourceAdmin;

	@Inject
	IRootResourceProvider		rootResourceProvider;

	@Configuration
	public Option[] config() {
		// FIXME Read mqnass features version from maven.
		// now mqnaas features version in this file must be changed manually in each release!
		return new Option[] {
				// distribution to test: Karaf 3.0.1
				KarafDistributionOption.karafDistributionConfiguration()
						.frameworkUrl(CoreOptions.maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").version("3.0.1"))
						.karafVersion("3.0.1").name("Apache Karaf").useDeployFolder(false)
						// keep deployed Karaf
						.unpackDirectory(new File("target/paxexam")),
				// no local and remote consoles
				KarafDistributionOption.configureConsole().ignoreLocalConsole(),
				KarafDistributionOption.configureConsole().ignoreRemoteShell(),
				// keep runtime folder allowing analysing results
				KarafDistributionOption.keepRuntimeFolder(),
				// use custom logging configuration file with a custom appender
				KarafDistributionOption.replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", new File(
						"src/test/resources/org.ops4j.pax.logging.cfg")),
				// maintain our log configuration
				KarafDistributionOption.doNotModifyLogConfiguration(),
				// add network features
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas.extensions").artifactId("network").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "network"),
				// add tson features
				KarafDistributionOption.features(CoreOptions.maven().groupId("net.i2cat.dana.tson").artifactId("tson").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "tson"),
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas").artifactId("mqnaas").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "mqnaas-wiremock"),
		// debug option
		// KarafDistributionOption.debugConfiguration(),
		};

	}

	/**
	 * Creates:
	 * 
	 * <ul>
	 * <li>TSON resource with 2 ports.</li>
	 * <li>Network resource</li>
	 * </ul>
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws URISyntaxException
	 * @throws CapabilityNotFoundException
	 * @throws ResourceNotFoundException
	 * @throws IOException
	 */
	@Before
	public void prepareTest() throws InstantiationException, IllegalAccessException, URISyntaxException, CapabilityNotFoundException,
			ResourceNotFoundException, IOException {

		mockServer();

		Endpoint tsonEndpoint = new Endpoint(new URI("http://www.myfaketson.com/tson"));
		Endpoint nitosendpoint = new Endpoint(new URI("http://localhost:8080"));

		// 1. create resources

		// // 1.a create physical network
		IRootResource network = rootResourceAdmin.createRootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));
		networkResource = new Network(network, serviceProvider);

		// // 1.b create physical tson (in physical network)
		IRootResource tson = networkResource.createResource(new Specification(Type.TSON), Arrays.asList(tsonEndpoint));
		tsonResource = new NetworkSubResource(tson, serviceProvider);

		// // 1.c create nitos network
		IRootResource nitos = networkResource.createResource(new Specification(Type.NETWORK, "nitos"), Arrays.asList(nitosendpoint));
		nitosResource = new Network(nitos, serviceProvider);

		// 2. create resources ports
		// // 2.1. create tson ports
		PortResourceWrapper port1Wrapper = new PortResourceWrapper(tsonResource.createPort(), serviceProvider);
		PortResourceWrapper port2Wrapper = new PortResourceWrapper(tsonResource.createPort(), serviceProvider);
		PortResourceWrapper port3Wrapper = new PortResourceWrapper(tsonResource.createPort(), serviceProvider);
		port1Wrapper.setAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE, TSON_PORT_1);
		port2Wrapper.setAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE, TSON_PORT_2);
		port3Wrapper.setAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE, TSON_PORT_3);

		// 3 define tson slice
		IResource tsonSliceResource = tsonResource.getSlice();
		Slice tsonSlice = new Slice(tsonSliceResource, serviceProvider);

		Unit portUnit = tsonSlice.addUnit("port");

		portUnit.setRange(new Range(0, 2));

		Cube cube = new Cube();
		cube.setRanges(new Range[] { new Range(0, 2) });
		tsonSlice.setCubes(Arrays.asList(cube));

	}

	@Test
	public void basicNetworkCreationTest() throws InstantiationException, IllegalAccessException, CapabilityNotFoundException,
			NetworkCreationException, ResourceNotFoundException, NetworkReleaseException {

		// 1. create request
		IResource requestResource = networkResource.createRequest();

		Request request = new Request(requestResource, serviceProvider);

		// 2. fill request

		// // 2.1 add request resources and mapping

		// // // 2.1.1 tson
		IResource reqTsonResource = request.createResource(Type.TSON);
		NetworkSubResource reqTson = new NetworkSubResource(reqTsonResource, serviceProvider);
		request.defineMapping(reqTsonResource, tsonResource.getResource());

		// // // 2.2.2 nitos
		IResource reqNitosResource = request.createResource(Type.NETWORK);
		Network reqNitos = new Network(reqNitosResource, serviceProvider);
		request.defineMapping(reqNitosResource, nitosResource.getNetworkResource());

		// // // 2.2.3 ofswitch in nitos
		IRootResource phyOfSwitch = nitosResource.getRootResources().get(0);
		IResource ofswitch = reqNitos.createRequestResource(Type.OF_SWITCH);
		request.defineMapping(ofswitch, phyOfSwitch);

		// // 2.2 specify internal ports

		IResource reqPort1 = reqTson.createPort();
		IResource tsonPort1 = tsonResource.getPorts().get(0);
		request.defineMapping(reqPort1, tsonPort1);

		IResource reqPort2 = reqTson.createPort();
		IResource tsonPort2 = tsonResource.getPorts().get(1);
		request.defineMapping(reqPort2, tsonPort2);

		// // 2.3 specify network external ports.

		IResource reqPort3 = reqTson.createPort();
		IResource tsonPort3 = tsonResource.getPorts().get(2);
		request.defineMapping(reqPort3, tsonPort3);
		request.addNetworkPort(reqPort3);

		// // 2.4 specify links
		IResource reqLinkResource = request.createLink();
		Link reqLink = new Link(reqLinkResource, serviceProvider);
		reqLink.setSrcPort(reqPort1);
		reqLink.setDstPort(reqPort2);

		// // 2.5 create Slice - first version contains whole slice
		IResource reqTsonSliceResource = reqTson.getSlice();
		Slice reqTsonSlice = new Slice(reqTsonSliceResource, serviceProvider);

		Unit portUnit = reqTsonSlice.addUnit("port");
		portUnit.setRange(new Range(0, 2));

		Cube cube = new Cube();
		cube.setRanges(new Range[] { new Range(0, 2) });
		reqTsonSlice.setCubes(Arrays.asList(cube));

		// // 2.6 add request period
		long currentTime = System.currentTimeMillis();
		Period period = new Period(new Date(currentTime), new Date(currentTime + 2000000L));
		request.setPeriod(period);

		// 3. send request to create network

		IRequestBasedNetworkManagement requestNetworkManagementCapab = serviceProvider.getCapability(networkResource.getNetworkResource(),
				IRequestBasedNetworkManagement.class);

		IRootResource networkResource = requestNetworkManagementCapab.createNetwork(request.getRequestResource());

		Network network = new Network(networkResource, serviceProvider);

		// Asserts!!

		// assert network resource and bound capabilities
		Assert.assertNotNull("A network instance should have been created from the request.", network);
		Assert.assertNotNull("Created network resource should contain a bound INetworkPortManagement Capability",
				network.getCapability(INetworkPortManagement.class));
		Assert.assertNotNull("Created network resource should contain a bound ILinkManagement Capability",
				network.getCapability(ILinkManagement.class));
		Assert.assertNotNull("Created network resource should contain a bound IRootResourceProvider Capability",
				network.getCapability(IRootResourceProvider.class));
		Assert.assertNotNull("Created network resource should contain a bound IRequestBasedNetworkManagement Capability",
				network.getCapability(IRequestBasedNetworkManagement.class));
		Assert.assertNotNull("Created network resource should contain a bound IRequestManagement Capability",
				network.getCapability(IRequestManagement.class));

		List<IRootResource> netResources = network.getRootResources();
		Assert.assertNotNull("Network should contain two resources.", netResources);
		Assert.assertEquals("Network should contain two resources.", 2, netResources.size());

		List<IRootResource> tsonResources = network.getRootResources(Type.TSON, null, null);
		Assert.assertEquals("Network should contain 1 tson Resource.", 1, tsonResources.size());

		List<IRootResource> virtNetworksResources = network.getRootResources(Type.NETWORK, "virtual", null);
		Assert.assertEquals("Network should contain 1 virtual network resource.", 1, virtNetworksResources.size());

		// get the virtual TSON
		IResource virtualTsonResource = network.getRootResources(Type.TSON, null, null).get(0);

		NetworkSubResource virtualTson = new NetworkSubResource(virtualTsonResource, serviceProvider);

		Assert.assertEquals("Network should contain the virtual TSON.", virtualTson.getResource(), netResources.get(0));

		// ports asserts
		List<IResource> virtualTsonPorts = virtualTson.getPorts();
		Assert.assertEquals("Virtual Tson should contain three ports.", 3, virtualTsonPorts.size());

		PortResourceWrapper virtTsonPort1 = new PortResourceWrapper(virtualTsonPorts.get(0), serviceProvider);
		PortResourceWrapper virtTsonPort2 = new PortResourceWrapper(virtualTsonPorts.get(1), serviceProvider);
		PortResourceWrapper virtTsonPort3 = new PortResourceWrapper(virtualTsonPorts.get(2), serviceProvider);

		Assert.assertEquals("Virtual tson port should contain translation to device port.", TSON_PORT_1,
				virtTsonPort1.getAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE));
		Assert.assertEquals("Virtual tson port should contain translation to device port.", TSON_PORT_2,
				virtTsonPort2.getAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE));
		Assert.assertEquals("Virtual tson port should contain translation to device port.", TSON_PORT_3,
				virtTsonPort3.getAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE));

		// slice asserts
		IResource virtualTsonSliceResource = virtualTson.getSlice();
		Slice virtualTsonSlice = new Slice(virtualTsonSliceResource, serviceProvider);

		Assert.assertEquals("Virtual Tson should contain created slice cube.", Arrays.asList(cube), virtualTsonSlice.getCubes());
		Assert.assertEquals("Virtual Tson should contain three ports.", "XXX", virtualTsonSlice.toMatrix());

		IResource phySliceResource = tsonResource.getSlice();
		Slice phySlice = new Slice(phySliceResource, serviceProvider);
		Assert.assertEquals("Virtual Tson should contain no ports.", "OOO", phySlice.toMatrix());

		// links asserts

		List<IResource> netLinks = network.getLinks();
		Assert.assertNotNull("Network should contain one link.", netLinks);
		Assert.assertNotNull("Network should contain one link.", netLinks.size());

		Link netlink = new Link(netLinks.get(0), serviceProvider);

		Assert.assertNotNull("Link should contain a source port.", netlink.getSrcPort());
		Assert.assertNotNull("Link should contain a destination port", netlink.getDstPort());
		Assert.assertEquals(tsonPort1, netlink.getSrcPort());
		Assert.assertEquals(tsonPort2, netlink.getDstPort());

		// networkports asserts

		List<IResource> netPorts = network.getPorts();
		Assert.assertNotNull("Network should contain an external port.", netPorts);
		Assert.assertEquals("Network should contain an external port.", 1, netPorts.size());
		Assert.assertEquals("Network should contain tsonPort3 as external port.", tsonPort3, netPorts.get(0));

		// subnetwork asserts
		// get the virtual network
		IResource virtualNetworkResource = network.getRootResources(Type.NETWORK, "virtual", null).get(0);
		Network virtualNetwork = new Network(virtualNetworkResource, serviceProvider);

		// get openflow switch from virtual network
		List<IRootResource> subnetResources = virtualNetwork.getRootResources();
		Assert.assertNotNull("Virtual subNetwork should contain 1 resource.", subnetResources);
		Assert.assertEquals("Virtual subNetwork should contain 1 resource.", 1, subnetResources.size());

		List<IRootResource> virtualSwitches = virtualNetwork.getRootResources(Type.OF_SWITCH, null, null);
		Assert.assertNotNull("Virtual subNetwork should contain 1 openflow switch.", virtualSwitches);
		Assert.assertEquals("Virtual subNetwork should contain 1 openflow switch.", 1, virtualSwitches.size());

		NetworkSubResource ofSwitch = new NetworkSubResource(virtualSwitches.get(0), serviceProvider);

		// 4. remove network
		requestNetworkManagementCapab.releaseNetwork(networkResource);

		// Asserts

		// // assert network no longer exists
		Assert.assertFalse("Network should no longer exists after its release.", requestNetworkManagementCapab.getNetworks()
				.contains(networkResource));

		// // slice asserts

		Slice tsonSlice = new Slice(tsonResource.getSlice(), serviceProvider);
		Assert.assertEquals("Physical TSON should contain original slice information again.", "XXX", tsonSlice.toMatrix());
	}

	private LeaseResourcesResponse generateLeaseResourcesResponse() {

		LeaseResourcesResponse response = new LeaseResourcesResponse();
		ResourceResponse resourceResponse = new ResourceResponse();

		List<Resource> resources = new ArrayList<Resource>();

		Resource resource = new Resource();

		resource.setName("ofswitch");
		resource.setType(net.i2cat.dana.nitos.client.model.Type.OF_SWITCH);
		resource.setHref("http://www.myfakenitos.com/ofswitch");

		resource.setUuid(Type.OF_SWITCH + "-1");

		resources.add(resource);

		resourceResponse.setResources(resources);
		response.setResourceResponse(resourceResponse);

		return response;
	}

	private void mockServer() throws IOException {

		String getResourcesresponse = textFileToString("/mock/getResourcesNitosResponse.json");
		String leaseResourcesResponse = textFileToString("/mock/leaseResourceNitosResponse.json");

		WireMock.stubFor(
				WireMock.get(
						WireMock.urlEqualTo("/resources/nodes"))
						.withHeader("Content-Type", WireMock.equalTo(MediaType.APPLICATION_JSON))
						.willReturn(WireMock.aResponse()
								.withStatus(HttpStatus.OK_200)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON)
								.withBody(getResourcesresponse)
						));

		WireMock.stubFor(
				WireMock.post(
						WireMock.urlEqualTo("/resources/leases"))
						.withHeader("Content-Type", WireMock.equalTo(MediaType.APPLICATION_JSON))
						.willReturn(WireMock.aResponse()
								.withStatus(HttpStatus.OK_200)
								.withHeader("Content-Type", MediaType.APPLICATION_JSON)
								.withBody(leaseResourcesResponse)
						)

				);
	}

	private String textFileToString(String fileLocation) throws IOException {
		String fileString = "";
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream(fileLocation)));
		String line;
		while ((line = br.readLine()) != null) {
			fileString += line += "\n";
		}
		br.close();
		return fileString;
	}
}
