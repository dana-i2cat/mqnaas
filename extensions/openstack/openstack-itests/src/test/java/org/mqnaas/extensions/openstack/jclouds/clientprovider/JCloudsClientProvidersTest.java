package org.mqnaas.extensions.openstack.jclouds.clientprovider;

/*
 * #%L
 * MQNaaS :: JClouds Client Provider
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.inject.Inject;

import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.clientprovider.api.client.IClientProvider;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICoreProvider;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.credentials.Credentials;
import org.mqnaas.core.api.credentials.UsernamePasswordTenantCredentials;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
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
 * jClouds {@link IClientProvider}'s tests.
 * 
 * @author Julio Carlos Barrera (i2CAT Foundation)
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JCloudsClientProvidersTest {

	private static final Logger			log	= LoggerFactory.getLogger(JCloudsClientProvidersTest.class);

	@Inject
	private ICoreProvider				coreProvider;

	@Inject
	private IServiceProvider			serviceProvider;

	@Inject
	private IRootResourceAdministration	rootResourceMgmt;

	private IClientProviderFactory		clientProviderFactory;

	private IRootResource				openStackResource;

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
				// add MQNaaS feature
				KarafDistributionOption.features(
						CoreOptions.maven().groupId("org.mqnaas").artifactId("mqnaas").classifier("features").type("xml").version("0.0.1-SNAPSHOT"),
						"mqnaas"),
				// add MQNaaS OpenStack feature
				KarafDistributionOption.features(
						CoreOptions.maven().groupId("org.mqnaas.extensions").artifactId("openstack").classifier("features").type("xml")
								.version("0.0.1-SNAPSHOT"), "mqnaas-openstack"),
				// install mqnaas-test-helpers bundle
				CoreOptions.mavenBundle().groupId("org.mqnaas").artifactId("mqnaas-test-helpers").version("0.0.1-SNAPSHOT"),
		// debug option
		// KarafDistributionOption.debugConfiguration()
		};
	}

	@Before
	public void init() throws CapabilityNotFoundException, InstantiationException, IllegalAccessException, URISyntaxException {
		IRootResource coreResource = coreProvider.getCore();
		clientProviderFactory = serviceProvider.getCapability(coreResource, IClientProviderFactory.class);

		openStackResource = createOpenStackResource(Type.CLOUD_MANAGER, "openstack", "http://example:12345/", "userName", "password", "tenantName");
	}

	@After
	public void removeResources() throws ResourceNotFoundException {
		rootResourceMgmt.removeRootResource(openStackResource);
	}

	@Test
	public void clientProviderTest() throws IllegalArgumentException, IllegalAccessException, CapabilityNotFoundException,
			ProviderNotFoundException, EndpointNotFoundException {
		// get client providers from factory
		IJCloudsNovaClientProvider jCloudsNovaClientProvider = clientProviderFactory.getClientProvider(IJCloudsNovaClientProvider.class);
		IJCloudsNeutronClientProvider jCloudsNeutronClientProvider = clientProviderFactory.getClientProvider(IJCloudsNeutronClientProvider.class);

		// get clients
		NovaApi novaClient = jCloudsNovaClientProvider.getClient(openStackResource);
		NeutronApi neutronClient = jCloudsNeutronClientProvider.getClient(openStackResource);

		// assert client instances non-nullity
		Assert.assertNotNull("Nova client must be defined", novaClient);
		Assert.assertNotNull("Neutron client must be defined", neutronClient);
	}

	private IRootResource createOpenStackResource(Specification.Type resourceType, String resourceModel, String endpointURI, String userName,
			String password, String tenantName) throws InstantiationException, IllegalAccessException, URISyntaxException {
		Specification spec = new Specification(resourceType, resourceModel);
		Endpoint endpoint = new Endpoint(new URI(endpointURI));
		Credentials credentials = new UsernamePasswordTenantCredentials(userName, password, tenantName);

		return rootResourceMgmt.createRootResource(RootResourceDescriptor.create(spec, Arrays.asList(endpoint), credentials));
	}
}
