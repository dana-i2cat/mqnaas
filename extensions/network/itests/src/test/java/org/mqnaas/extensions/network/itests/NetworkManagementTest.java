package org.mqnaas.extensions.network.itests;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
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
import org.mqnaas.network.impl.NetworkSubResource;
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

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class NetworkManagementTest {

	private static final Logger	log	= LoggerFactory.getLogger(NetworkManagementTest.class);

	private Network				networkResource;
	private NetworkSubResource	tsonResource;
	private NetworkSubResource	ofSwitchResource1;
	private NetworkSubResource	ofSwitchResource2;

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
		// debug option
		// KarafDistributionOption.debugConfiguration()
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
	 */
	@Before
	public void prepareTest() throws InstantiationException, IllegalAccessException, URISyntaxException, CapabilityNotFoundException,
			ResourceNotFoundException {

		Endpoint tsonEndpoint = new Endpoint(new URI("http://www.myfaketson.com/tson"));
		Endpoint nitosendpoint = new Endpoint(new URI("http://www.myfakenitos.com/nitos"));

		// 1. create resources

		// // 1.a create physical network
		IRootResource network = rootResourceAdmin.createRootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));
		networkResource = new Network(network, serviceProvider);

		// // 1.b create physical tson (in physical network)
		IRootResource tson = networkResource.createResource(new Specification(Type.TSON), Arrays.asList(tsonEndpoint));
		tsonResource = new NetworkSubResource(tson, serviceProvider);
		// 2. create resources ports
		// // 2.1. create tson ports
		tsonResource.createPort();
		tsonResource.createPort();
		tsonResource.createPort();

		// 3 define tson slice
		IResource tsonSliceResource = tsonResource.getSlice();
		Slice tsonSlice = new Slice(tsonSliceResource, serviceProvider);

		Unit portUnit = tsonSlice.addUnit("port");

		portUnit.setRange(new Range(0, 1));

		Cube cube = new Cube();
		cube.setRanges(new Range[] { new Range(0, 1) });
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

		IResource reqTsonResource = request.createResource(Type.TSON);
		NetworkSubResource reqTson = new NetworkSubResource(reqTsonResource, serviceProvider);
		request.defineMapping(reqTsonResource, tsonResource.getResource());

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
		portUnit.setRange(new Range(0, 1));

		Cube cube = new Cube();
		cube.setRanges(new Range[] { new Range(0, 1) });
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

		List<IRootResource> tsonResources = network.getRootResources(Type.TSON, null, null);
		Assert.assertEquals("Network should contain 1 tson Resources.", 1, tsonResources.size());

		// get the virtual TSON
		IResource virtualTsonResource = network.getRootResources(Type.TSON, null, null).get(0);

		NetworkSubResource virtualTson = new NetworkSubResource(virtualTsonResource, serviceProvider);

		List<IRootResource> netResources = network.getRootResources();
		Assert.assertNotNull("Network should contain a Tson resource.", netResources);
		Assert.assertEquals("Network should contain a Tson resource.", 1, netResources.size());
		Assert.assertEquals("Network should contain the virtual TSON.", virtualTson.getResource(), netResources.get(0));

		// slice asserts
		IResource virtualTsonSliceResource = virtualTson.getSlice();
		Slice virtualTsonSlice = new Slice(virtualTsonSliceResource, serviceProvider);

		Assert.assertEquals("Virtual Tson should contain created slice cube.", Arrays.asList(cube), virtualTsonSlice.getCubes());
		Assert.assertEquals("Virtual Tson should contain two ports.", "XX", virtualTsonSlice.toMatrix());

		IResource phySliceResource = tsonResource.getSlice();
		Slice phySlice = new Slice(phySliceResource, serviceProvider);
		Assert.assertEquals("Virtual Tson should contain no ports.", "OO", phySlice.toMatrix());

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

		// 4. remove network
		requestNetworkManagementCapab.releaseNetwork(networkResource);

		// Asserts

		// // assert network no longer exists
		Assert.assertFalse("Network should no longer exists after its release.", requestNetworkManagementCapab.getNetworks()
				.contains(networkResource));

		// // slice asserts

		Slice tsonSlice = new Slice(tsonResource.getSlice(), serviceProvider);
		Assert.assertEquals("Physical TSON should contain original slice information again.", "XX", tsonSlice.toMatrix());
	}
}
