package org.mqnaas.extensions.network.itests;

/*
 * #%L
 * MQNaaS :: Network Integration Tests
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

import java.io.File;
import java.util.Arrays;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICoreProvider;
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
import org.mqnaas.network.api.topology.link.ILinkAdministration;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.IPortManagement;
import org.mqnaas.network.impl.topology.link.LinkResource;
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
public class ResourcesIntegrationTest {

	private static final Logger	log	= LoggerFactory.getLogger(ResourcesIntegrationTest.class);

	@Inject
	IRootResourceAdministration	rootResourceMgmt;

	@Inject
	IServiceProvider			serviceProvider;

	@Inject
	IRootResourceProvider		rootResourceProv;

	@Inject
	ICoreProvider				coreProvider;

	IRootResource				coreResource;
	IRootResource				tsonResource;
	IRootResource				networkResource;

	@Configuration
	public Option[] config() {
		// FIXME Read mqnass features version from maven.
		// now mqnaas features version in this file must be changed manually in each release!
		return new Option[] {
				// distribution to test: Karaf 3.0.3
				KarafDistributionOption.karafDistributionConfiguration()
						.frameworkUrl(CoreOptions.maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").version("3.0.3"))
						.karafVersion("3.0.3").name("Apache Karaf").useDeployFolder(false)
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
				// add network feature
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas.extensions").artifactId("network").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "network"),
		// debug option
		// KarafDistributionOption.debugConfiguration()
		};
	}

	@Before
	public void createResources() throws InstantiationException, IllegalAccessException, InterruptedException {
		coreResource = coreProvider.getCore();
		networkResource = createRootResource(Type.NETWORK);

		tsonResource = createRootResource(Type.TSON);

	}

	@After
	public void removeResources() throws ResourceNotFoundException {
		rootResourceMgmt.removeRootResource(networkResource);
		rootResourceMgmt.removeRootResource(tsonResource);
	}

	@Test(expected = CapabilityNotFoundException.class)
	public void portManagementCoreBindingTest() throws CapabilityNotFoundException, InstantiationException, IllegalAccessException,
			ResourceNotFoundException, InterruptedException {

		serviceProvider.getCapability(coreResource, IPortManagement.class);

	}

	@Test(expected = CapabilityNotFoundException.class)
	public void portManagementNetworkBindingTest() throws CapabilityNotFoundException, InstantiationException, IllegalAccessException,
			ResourceNotFoundException {

		serviceProvider.getCapability(networkResource, IPortManagement.class);

	}

	@Test
	public void portManagementRestOfResourcesBindingTest() throws CapabilityNotFoundException, ResourceNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException {

		IPortManagement portManagementCapab = serviceProvider.getCapability(tsonResource, IPortManagement.class);

		Assert.assertNotNull("All RootResources, except network and core, should contain a bound IPortManagement capability.",
				portManagementCapab);

	}

	@Test(expected = CapabilityNotFoundException.class)
	public void linkManagementCoreBindingTest() throws CapabilityNotFoundException, InstantiationException, IllegalAccessException,
			ResourceNotFoundException, InterruptedException {

		serviceProvider.getCapability(coreResource, ILinkManagement.class);

	}

	@Test
	public void linkManagementNetworkBindingTest() throws CapabilityNotFoundException, ResourceNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException {

		// network resource
		ILinkManagement linkManagementCapab = serviceProvider.getCapability(networkResource, ILinkManagement.class);
		Assert.assertNotNull("Network resource should contain a bound ILinkManagement capability.", linkManagementCapab);

		// other resource, for example, tson
		linkManagementCapab = serviceProvider.getCapability(tsonResource, ILinkManagement.class);
		Assert.assertNotNull("All RootResources should contain a bound ILinkManagement capability.",
				linkManagementCapab);

	}

	@Test
	public void linkAdministrationBindingTest() throws CapabilityNotFoundException, ResourceNotFoundException, InstantiationException,
			IllegalAccessException, InterruptedException {

		ILinkManagement linkManagementCapab = serviceProvider.getCapability(tsonResource, ILinkManagement.class);
		Assert.assertNotNull("TSON resource should contain a bound ILinkManagement capability.", linkManagementCapab);

		// link resource capabilities
		IResource link = linkManagementCapab.createLink();
		Assert.assertNotNull(link);
		Assert.assertTrue(link instanceof LinkResource);

		ILinkAdministration linkAdminCapab = serviceProvider.getCapability(link, ILinkAdministration.class);
		Assert.assertNotNull("Link resource should contain a bound ILinkAdministration capability", linkAdminCapab);

		// remove created resources
		linkManagementCapab.removeLink(link);

	}

	private IRootResource createRootResource(Specification.Type resourceType) throws InstantiationException, IllegalAccessException {

		Specification spec = new Specification(resourceType);
		Endpoint endpoint = null;

		return rootResourceMgmt.createRootResource(RootResourceDescriptor.create(spec, Arrays.asList(endpoint)));

	}
}
