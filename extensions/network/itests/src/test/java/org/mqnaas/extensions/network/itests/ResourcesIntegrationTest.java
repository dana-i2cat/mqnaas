package org.mqnaas.extensions.network.itests;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.infrastructure.IInfrastructureProvider;
import org.mqnaas.network.api.topology.ITopologyProvider;
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
						.unpackDirectory(new File("target/exam")),
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

		ITopologyProvider topologyProviderCapab = serviceProvider.getCapability(networkResource, ITopologyProvider.class);
		Assert.assertNotNull("Network resource should contain a bounded ITopologyProvider capability.", topologyProviderCapab);

		IInfrastructureProvider infrastructureProviderCapab = serviceProvider.getCapability(networkResource, IInfrastructureProvider.class);
		Assert.assertNotNull("Network resource should contain a bounded IInfrastructureProvider capability.", infrastructureProviderCapab);

		rootResourceMgmt.removeRootResource(networkResource);

	}
}
