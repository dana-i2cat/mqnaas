package org.mqnaas.extensions.modelreader.itests;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.common.util.ProxyClassLoader;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.extensions.modelreader.api.IResourceModelReader;
import org.mqnaas.extensions.modelreader.api.ResourceModelWrapper;
import org.mqnaas.extensions.odl.helium.flowprogrammer.model.FlowConfig;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * 
 * @author Adrián Roselló Rey(i2CAT)
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ResourceModelReaderTest {

	@Rule
	public WireMockRule			wireMockRule					= new WireMockRule(8080);

	@Inject
	IRootResourceProvider		rootResourceProvider;

	@Inject
	IRootResourceAdministration	rootResourceAdministration;

	@Inject
	IServiceProvider			serviceProvider;

	private IRootResource		network;

	private static final String	OF_SWITCH_EXT_ID				= "00:00:00:00:00:00:00:02";

	private static final String	OFSWITCH_PORT1_EXT_ID			= "2";
	private static final String	OFSWITCH_PORT2_EXT_ID			= "1";

	private static final String	OFSWITCH_PORT1_EXT_NAME			= "eth1";
	private static final String	OFSWITCH_PORT2_EXT_NAME			= "eth0";

	private static final String	NODES_RESPONSE_FILE				= "/responses/nodeResponse.xml";
	private static final String	NODECONNECTORS_RESPONSE_FILE	= "/responses/nodeConnectorsResponse.xml";
	private static final String	FLOWS_RESPONSE_FILE				= "/responses/flows.xml";					;

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
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas.extensions").artifactId("odl").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "odl"),
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas.extensions").artifactId("modelreader")
						.classifier("features").type("xml").version("0.0.1-SNAPSHOT"), "mqnaas-modelreader"),
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas").artifactId("mqnaas")
						.classifier("features").type("xml").version("0.0.1-SNAPSHOT"), "mqnaas-wiremock"),
		// debug option
		// KarafDistributionOption.debugConfiguration()
		};
	}

	@Before
	public void startResources() throws InstantiationException, IllegalAccessException, URISyntaxException, CapabilityNotFoundException, IOException {

		mockODLInstance();

		// create network
		Endpoint endpoint = new Endpoint(new URI("http://localhost:8080"));
		network = rootResourceAdministration.createRootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK, "odl"),
				Arrays.asList(endpoint)));

	}

	@After
	public void removeResources() throws ResourceNotFoundException {

		rootResourceAdministration.removeRootResource(network);
	}

	@Test
	public void modelReaderTest() throws URISyntaxException, InstantiationException, IllegalAccessException, CapabilityNotFoundException {

		IResourceModelReader modelReader = serviceProvider.getCapability(network, IResourceModelReader.class);
		Assert.assertNotNull("Network should contain a IResourceModelReader capability.", modelReader);

		ResourceModelWrapper networkModel = modelReader.getResourceModel();

		checkResourceModelReaderCapabililityResponse(networkModel);

	}

	@Test
	public void modelReaderWSTest() {

		String networkId = network.getId();
		IResourceModelReader wsClient = createClient("http://localhost:9000/mqnaas/IRootResourceAdministration/" + networkId + "/IResourceModelReader/resourceModel");

		ResourceModelWrapper networkModel = wsClient.getResourceModel();
		checkResourceModelReaderCapabililityResponse(networkModel);
	}

	private void checkResourceModelReaderCapabililityResponse(ResourceModelWrapper networkModel) {
		// Network resource asserts
		Assert.assertNotNull("Network model should not be null.", networkModel);
		Assert.assertEquals("Network resource reporesentation in model should contain the same id as the real network", network.getId(),
				networkModel.getId());
		Assert.assertEquals("Network resource reporesentation in model should contain the same type as the network", network.getDescriptor()
				.getSpecification().getType().toString(), networkModel.getType());
		Assert.assertEquals("Network model representation should contain 1 subresource.", 1, networkModel.getResources().size());

		// network openflow rules
		Assert.assertNotNull("Network model should contain two configured rules.", networkModel.getConfiguredRules());
		Assert.assertNotNull("Network model should contain two configured rules.", networkModel.getConfiguredRules().getFlowConfig());
		Assert.assertEquals("Network model should contain two configured rules.", 2, networkModel.getConfiguredRules().getFlowConfig().size());
		FlowConfig firstFlow = networkModel.getConfiguredRules().getFlowConfig().get(0);
		FlowConfig secondFlow = networkModel.getConfiguredRules().getFlowConfig().get(1);
		Assert.assertNotNull("First openflow rule of the network should not be null.", firstFlow);
		Assert.assertNotNull("First openflow rule of the network should not be null.", secondFlow);

		// Switch resource asserts
		ResourceModelWrapper switchModel = networkModel.getResources().get(0);
		Assert.assertNotNull("Switch model should not be null.", switchModel);
		Assert.assertEquals("Switch resource representation in model should be of type " + Type.OF_SWITCH, Type.OF_SWITCH.toString(),
				switchModel.getType());
		Assert.assertEquals("Switch model should contain as external id " + OF_SWITCH_EXT_ID, OF_SWITCH_EXT_ID,
				switchModel.getAttributes().get(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("Switch model representation should contain 2 subresources.", 2, switchModel.getResources().size());
		Assert.assertNull("Switch model should not contain any openflow rules!", switchModel.getConfiguredRules());

		// Switch ports asserts
		ResourceModelWrapper port1Model = switchModel.getResources().get(0);
		ResourceModelWrapper port2Model = switchModel.getResources().get(1);
		Assert.assertFalse("Both model ports should be different.", port1Model.equals(port2Model));

		Assert.assertEquals("First model port should be of type port.", "port", port1Model.getType());
		Assert.assertEquals("Second model port should be of type port.", "port", port2Model.getType());

		Assert.assertTrue("First model port should not contain subresources.", port1Model.getResources().isEmpty());
		Assert.assertTrue("Second model port should not contain subresources.", port2Model.getResources().isEmpty());

		Assert.assertNull("Ports should not contain any openflow rule!", port1Model.getConfiguredRules());
		Assert.assertNull("Ports should not contain any openflow rule!", port2Model.getConfiguredRules());

		Assert.assertEquals("First model port should contain the expected external port id. ", OFSWITCH_PORT1_EXT_ID,
				port1Model.getAttributes().get(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("First model port should contain the expected external port name. ", OFSWITCH_PORT1_EXT_NAME,
				port1Model.getAttributes().get(IAttributeStore.RESOURCE_EXTERNAL_NAME));

		Assert.assertEquals("Second model port should contain the expected external port id. ", OFSWITCH_PORT2_EXT_ID, port2Model.getAttributes()
				.get(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("Second model port should contain the expected external port name. ", OFSWITCH_PORT2_EXT_NAME,
				port2Model.getAttributes().get(IAttributeStore.RESOURCE_EXTERNAL_NAME));
	}

	private IResourceModelReader createClient(String addressUri) {

		// create CXF client
		ProxyClassLoader classLoader = new ProxyClassLoader(IResourceModelReader.class.getClassLoader());
		classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(addressUri);
		bean.setResourceClass(IResourceModelReader.class);
		bean.setClassLoader(classLoader);

		return (IResourceModelReader) bean.create();
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

	private void mockODLInstance() throws IOException {

		WireMock.stubFor(
				WireMock.get(
						WireMock.urlEqualTo("/controller/nb/v2/switchmanager/default/nodes"))
						.withHeader("Content-Type", WireMock.equalTo(MediaType.APPLICATION_XML))
						.willReturn(WireMock.aResponse()
								.withStatus(200)
								.withHeader("Content-Type", MediaType.APPLICATION_XML)
								.withBody(textFileToString(NODES_RESPONSE_FILE))
						));

		WireMock.stubFor(
				WireMock.get(
						WireMock.urlEqualTo("/controller/nb/v2/switchmanager/default/node/OF/" + OF_SWITCH_EXT_ID))
						.withHeader("Content-Type", WireMock.equalTo(MediaType.APPLICATION_XML))
						.willReturn(WireMock.aResponse()
								.withStatus(200)
								.withHeader("Content-Type", MediaType.APPLICATION_XML)
								.withBody(textFileToString(NODECONNECTORS_RESPONSE_FILE))
						));

		WireMock.stubFor(
				WireMock.get(
						WireMock.urlEqualTo("/controller/nb/v2/flowprogrammer/default"))
						.withHeader("Content-Type", WireMock.equalTo(MediaType.APPLICATION_XML))
						.willReturn(WireMock.aResponse()
								.withStatus(200)
								.withHeader("Content-Type", MediaType.APPLICATION_XML)
								.withBody(textFileToString(FLOWS_RESPONSE_FILE))
						));
	}
}
