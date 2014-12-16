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
import org.mqnaas.core.api.slicing.ISliceAdministration;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.api.slicing.Unit;
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

	private IRootResource		networkResource;
	private IRootResource		tsonResource;
	private IRootResource		nitosResource;

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
	 */
	@Before
	public void prepareTest() throws InstantiationException, IllegalAccessException, URISyntaxException, CapabilityNotFoundException {

		Endpoint tsonEndpoint = new Endpoint(new URI("http://www.myfaketson.com/tson"));

		// create resources
		tsonResource = rootResourceAdmin.createRootResource(RootResourceDescriptor.create(new Specification(Type.TSON), Arrays.asList(tsonEndpoint)));
		networkResource = rootResourceAdmin.createRootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));
		nitosResource = rootResourceAdmin.createRootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK, "nitos")));

		// define tson slice
		ISliceProvider sliceProvider = serviceProvider.getCapability(tsonResource, ISliceProvider.class);
		IResource slice = sliceProvider.getSlice();
		ISliceAdministration sliceAdmin = serviceProvider.getCapability(slice, ISliceAdministration.class);

		Unit portUnit = new Unit("port");
		sliceAdmin.addUnit(portUnit);
		sliceAdmin.setRange(portUnit, new Range(0, 1));

		Cube cube = new Cube();
		cube.setRanges(new Range[] { new Range(0, 1) });
		sliceAdmin.setCubes(Arrays.asList(cube));

		// define tson ports
		IPortManagement tsonPortManagement = serviceProvider.getCapability(tsonResource, IPortManagement.class);
		tsonPortManagement.createPort();
		tsonPortManagement.createPort();

	}

	@Test
	public void basicNetworkCreationTest() throws InstantiationException, IllegalAccessException, CapabilityNotFoundException,
			NetworkCreationException, ResourceNotFoundException {

		// 1. create request
		IRequestManagement requestMgmCapab = serviceProvider.getCapability(networkResource, IRequestManagement.class);
		IResource request = requestMgmCapab.createRequest();

		// 2. get request capabilities
		IRequestResourceMapping mappingCapab = serviceProvider.getCapability(request, IRequestResourceMapping.class);
		ILinkManagement linkMgmCapab = serviceProvider.getCapability(request, ILinkManagement.class);
		IRequestAdministration requestAdminCapab = serviceProvider.getCapability(request, IRequestAdministration.class);
		IRequestResourceManagement reqResourceMgm = serviceProvider.getCapability(request, IRequestResourceManagement.class);

		// 3. fill request

		// // 3.1 add request resources and mapping

		IResource reqTsonResource = reqResourceMgm.createResource(Type.TSON);
		mappingCapab.defineMapping(reqTsonResource, tsonResource);

		// // 3.2 specify request ports
		IPortManagement portMgmCapab = serviceProvider.getCapability(reqTsonResource, IPortManagement.class);

		IResource reqPort1 = portMgmCapab.createPort();
		IResource tsonPort1 = serviceProvider.getCapability(tsonResource, IPortManagement.class).getPorts().get(0);
		mappingCapab.defineMapping(reqPort1, tsonPort1);

		IResource reqPort2 = portMgmCapab.createPort();
		IResource tsonPort2 = serviceProvider.getCapability(tsonResource, IPortManagement.class).getPorts().get(1);
		mappingCapab.defineMapping(reqPort2, tsonPort2);

		// // 3.3 specify links
		IResource reqLink = linkMgmCapab.createLink();
		ILinkAdministration linkAdminCapab = serviceProvider.getCapability(reqLink, ILinkAdministration.class);
		linkAdminCapab.setSrcPort(reqPort1);
		linkAdminCapab.setDestPort(reqPort2);

		// // 3.4 create Slice - first version contains whole slice
		ISliceProvider reqTsonSliceCapab = serviceProvider.getCapability(reqTsonResource, ISliceProvider.class);
		IResource reqTsonSlice = reqTsonSliceCapab.getSlice();
		ISliceAdministration reqtTsonSliceAdmin = serviceProvider.getCapability(reqTsonSlice, ISliceAdministration.class);

		Unit portUnit = new Unit("port");
		reqtTsonSliceAdmin.addUnit(portUnit);
		reqtTsonSliceAdmin.setRange(portUnit, new Range(0, 1));

		Cube cube = new Cube();
		cube.setRanges(new Range[] { new Range(0, 1) });
		reqtTsonSliceAdmin.setCubes(Arrays.asList(cube));

		// // 3.5 add request period
		long currentTime = System.currentTimeMillis();
		Period period = new Period(new Date(currentTime), new Date(currentTime + 2000000L));
		requestAdminCapab.setPeriod(period);

		// 4. send request to create network

		IRequestBasedNetworkManagement requestNetworkManagementCapab = serviceProvider.getCapability(networkResource,
				IRequestBasedNetworkManagement.class);

		IRootResource network = requestNetworkManagementCapab.createNetwork(request);

		// Asserts!!

		// assert network resource and bound capabilities
		Assert.assertNotNull("A network instance should have been created from the request.", network);
		Assert.assertNotNull("Created network resource should contain a bound INetworkPortManagement Capability",
				serviceProvider.getCapability(network, INetworkPortManagement.class));
		Assert.assertNotNull("Created network resource should contain a bound ILinkManagement Capability",
				serviceProvider.getCapability(network, ILinkManagement.class));
		Assert.assertNotNull("Created network resource should contain a bound IRootResourceProvider Capability",
				serviceProvider.getCapability(network, IRootResourceProvider.class));
		Assert.assertNotNull("Created network resource should contain a bound IRootResourceAdministration Capability",
				serviceProvider.getCapability(network, IRootResourceAdministration.class));
		Assert.assertNotNull("Created network resource should contain a bound IRequestBasedNetworkManagement Capability",
				serviceProvider.getCapability(network, IRequestBasedNetworkManagement.class));
		Assert.assertNotNull("Created network resource should contain a bound IRequestManagement Capability",
				serviceProvider.getCapability(network, IRequestManagement.class));

		List<IRootResource> tsonResources = rootResourceProvider.getRootResources(Type.TSON, null, null);
		Assert.assertEquals("Platform should contain 2 tson Resources: the original one and the slice.", 2, tsonResources.size());

		// get the virtual TSON
		IResource virtualTson = (tsonResource == tsonResources.get(0)) ? tsonResources.get(1) : tsonResources.get(0);

		// slice asserts
		ISliceProvider virtualTsonSliceProvider = serviceProvider.getCapability(virtualTson, ISliceProvider.class);
		IResource virtualTsonSlice = virtualTsonSliceProvider.getSlice();

		ISliceAdministration virtualTsonSliceAdmin = serviceProvider.getCapability(virtualTsonSlice, ISliceAdministration.class);
		Assert.assertEquals("Virtual Tson should contain created slice cube.", Arrays.asList(cube), virtualTsonSliceAdmin.getCubes());
		Assert.assertEquals("Virtual Tson should contain two ports.", "XX", virtualTsonSliceAdmin.toString());

		ISliceProvider phySliceProvider = serviceProvider.getCapability(tsonResource, ISliceProvider.class);
		IResource phySlice = phySliceProvider.getSlice();
		ISliceAdministration phySliceAdmin = serviceProvider.getCapability(phySlice, ISliceAdministration.class);
		Assert.assertEquals("Virtual Tson should contain no ports.", "OO", phySliceAdmin.toString());

		// links asserts
		ILinkManagement netLinkManagement = serviceProvider.getCapability(network, ILinkManagement.class);
		List<IResource> netLinks = netLinkManagement.getLinks();
		Assert.assertNotNull("Network should contain one link.", netLinks);
		Assert.assertNotNull("Network should contain one link.", netLinks.size());

		ILinkAdministration linkAdministration = serviceProvider.getCapability(netLinks.get(0), ILinkAdministration.class);
		Assert.assertNotNull("Link should contain a source port.", linkAdministration.getSrcPort());
		Assert.assertNotNull("Link should contain a destination port", linkAdministration.getDestPort());
		Assert.assertEquals(tsonPort1, linkAdministration.getSrcPort());
		Assert.assertEquals(tsonPort2, linkAdministration.getDestPort());

	}
}
