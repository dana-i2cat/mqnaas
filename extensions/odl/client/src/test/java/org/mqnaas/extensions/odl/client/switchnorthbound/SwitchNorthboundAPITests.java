package org.mqnaas.extensions.odl.client.switchnorthbound;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnectors;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.Nodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * 
 * @author Julio Carlos Barrera
 *
 */
public class SwitchNorthboundAPITests {

	private static final Logger	log							= LoggerFactory.getLogger(SwitchNorthboundAPITests.class);

	private final static String	TEST_NODES_FILE				= "/serialization/test-nodes.xml";
	private final static String	TEST_NODE_CONNECTORS_FILE	= "/serialization/test-node-connectors.xml";

	@Rule
	public WireMockRule			wireMockRule				= new WireMockRule(8080);

	@Test
	public void testGetSwitches() {
		ISwitchNorthboundAPI client = getClient("http://localhost:8080/", "admin", "admin");
		Nodes obtainedNodes = client.getNodes("default");
		log.info("Obtained Nodes:\n" + obtainedNodes);

		Nodes expectedNodes = NodesSerializationTests.generateSampleNodes();
		log.info("Expected Nodes:\n" + expectedNodes);

		Assert.assertEquals("Deserialized nodes must be equals to the sample one.", expectedNodes, obtainedNodes);
	}

	@Test
	public void testGetPorts() {
		ISwitchNorthboundAPI client = getClient("http://localhost:8080/", "admin", "admin");
		NodeConnectors obtainedNodeConnectors = client.getNodeConnectors("default", "OF", "00:00:00:00:00:00:00:01");
		log.info("Obtained Node Connectors:\n" + obtainedNodeConnectors);

		NodeConnectors expectedNodeConnectors = NodeConnectorsSerializationTests.generateSampleNodeConnectors();
		log.info("Expected Node Connectors:\n" + expectedNodeConnectors);

		Assert.assertEquals("Deserialized node connectors must be equals to the sample one.", expectedNodeConnectors, obtainedNodeConnectors);
	}

	@Before
	public void mockServer() throws IOException {
		// get nodes test response body
		String getNodesResponse = IOUtils.toString(this.getClass().getResourceAsStream(TEST_NODES_FILE));

		// get nodes endpoint
		WireMock.stubFor(
				WireMock.get(
						WireMock.urlEqualTo("/controller/nb/v2/switchmanager/default/nodes"))
						.willReturn(WireMock.aResponse()
								.withStatus(200)
								.withHeader("Content-Type", MediaType.APPLICATION_XML)
								.withHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
								.withBody(getNodesResponse)
						));

		// get node connectors test response body
		String getNodeConnectorsResponse = IOUtils.toString(this.getClass().getResourceAsStream(TEST_NODE_CONNECTORS_FILE));

		// get nodes connectors endpoint
		WireMock.stubFor(
				WireMock.get(
						WireMock.urlEqualTo("/controller/nb/v2/switchmanager/default/node/OF/00:00:00:00:00:00:00:01"))
						.willReturn(WireMock.aResponse()
								.withStatus(200)
								.withHeader("Content-Type", MediaType.APPLICATION_XML)
								.withHeader("Authorization", "Basic YWRtaW46YWRtaW4=")
								.withBody(getNodeConnectorsResponse)
						));
	}

	private static ISwitchNorthboundAPI getClient(String baseURL, String username, String password) {
		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(baseURL);
		bean.setResourceClass(ISwitchNorthboundAPI.class);

		bean.setUsername(username);
		bean.setPassword(password);

		return bean.create(ISwitchNorthboundAPI.class);
	}
}
