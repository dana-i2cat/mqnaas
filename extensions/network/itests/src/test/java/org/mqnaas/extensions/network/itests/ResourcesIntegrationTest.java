package org.mqnaas.extensions.network.itests;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.infrastructure.IInfrastructureAdministration;
import org.mqnaas.network.api.infrastructure.IInfrastructureProvider;
import org.mqnaas.network.api.topology.ITopologyProvider;
import org.mqnaas.network.api.topology.device.IDeviceAdministration;
import org.mqnaas.network.api.topology.device.IDeviceManagement;
import org.mqnaas.network.api.topology.device.IPortAdministration;
import org.mqnaas.network.api.topology.device.IPortManagement;
import org.mqnaas.network.api.topology.link.ILinkAdministration;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.impl.infrastructure.InfrastructureResource;
import org.mqnaas.network.impl.topology.TopologyResource;
import org.mqnaas.network.impl.topology.device.DeviceResource;
import org.mqnaas.network.impl.topology.device.PortResource;
import org.mqnaas.network.impl.topology.link.LinkResource;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ResourcesIntegrationTest {

	@Inject
	IRootResourceManagement	rootResourceMgmt;

	@Inject
	IServiceProvider		serviceProvider;

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
				// add network feature
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas.extensions").artifactId("network").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "network"),
		// debug option
		// KarafDistributionOption.debugConfiguration()
		};
	}

	@Test
	public void networkResourceCapabilityTest() throws CapabilityNotFoundException {

		Specification spec = new Specification(Type.NETWORK);

		IRootResource networkResource = rootResourceMgmt.createRootResource(spec, new ArrayList<Endpoint>());

		// network resource capabilities.
		ITopologyProvider topologyProviderCapab = serviceProvider.getCapability(networkResource, ITopologyProvider.class);
		Assert.assertNotNull("Network resource should contain a bound ITopologyProvider capability.", topologyProviderCapab);

		IInfrastructureProvider infrastructureProviderCapab = serviceProvider.getCapability(networkResource, IInfrastructureProvider.class);
		Assert.assertNotNull("Network resource should contain a bound IInfrastructureProvider capability.", infrastructureProviderCapab);

		IResource topology = topologyProviderCapab.getTopology();
		Assert.assertNotNull("Network should contain a topology, provided by the ITopologyProvider capability.", topology);
		Assert.assertTrue("TopologyProvider capability should provide topology resources.", topology instanceof TopologyResource);

		IResource infrastructure = infrastructureProviderCapab.getInfrastructure();
		Assert.assertNotNull("Network should contain an infrastructure, provided by the IInfrastructureProvider capability.", infrastructure);
		Assert.assertTrue("InfrastructureProvider capability should provide infrastructure resources.",
				infrastructure instanceof InfrastructureResource);

		// infrastructure resource capabilities
		IInfrastructureAdministration infrastructureAdminCapab = serviceProvider.getCapability(infrastructure, IInfrastructureAdministration.class);
		Assert.assertNotNull("Infrastructure resource should contain a bound IInfrastructureAdministration capability.", infrastructureAdminCapab);

		// topology resource capabilities
		IDeviceManagement deviceManagementCapab = serviceProvider.getCapability(topology, IDeviceManagement.class);
		Assert.assertNotNull("Topology resource should contain a bound IDeviceManagement capability.", deviceManagementCapab);

		ILinkManagement linkManagementCapab = serviceProvider.getCapability(topology, ILinkManagement.class);
		Assert.assertNotNull("Topology resource should contain a bound ILinkManagement capability.", linkManagementCapab);

		IPortManagement portManagementCapab = serviceProvider.getCapability(topology, IPortManagement.class);
		Assert.assertNotNull("Topology resource should contain a bound IPortManagement capability.", portManagementCapab);

		// link resource capabilities
		IResource link = linkManagementCapab.createLink();
		Assert.assertNotNull(link);
		Assert.assertTrue(link instanceof LinkResource);

		ILinkAdministration linkAdminCapab = serviceProvider.getCapability(link, ILinkAdministration.class);
		Assert.assertNotNull("Link resource should contain a bound ILinkAdministration capability", linkAdminCapab);

		// device resource capabilities
		IResource device = deviceManagementCapab.createDevice();
		Assert.assertNotNull(device);
		Assert.assertTrue(device instanceof DeviceResource);

		IDeviceAdministration deviceAdminCapab = serviceProvider.getCapability(link, IDeviceAdministration.class);
		Assert.assertNotNull("Device resource should contain a bound IDeviceAdministration capability", deviceAdminCapab);

		// port resource capabilities
		IResource port = portManagementCapab.createPort();
		Assert.assertNotNull(port);
		Assert.assertTrue(port instanceof PortResource);

		IPortAdministration portAdminCapab = serviceProvider.getCapability(link, IPortAdministration.class);
		Assert.assertNotNull("Device resource should contain a bound IPortAdministration capability", portAdminCapab);

		// remove all resources
		portManagementCapab.removePort(port);
		linkManagementCapab.removeLink(link);
		deviceManagementCapab.removeDevice(device);

		rootResourceMgmt.removeRootResource(networkResource);

	}
}
