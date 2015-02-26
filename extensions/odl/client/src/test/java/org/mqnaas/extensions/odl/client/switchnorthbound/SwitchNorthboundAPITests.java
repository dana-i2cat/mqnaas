package org.mqnaas.extensions.odl.client.switchnorthbound;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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

	private static final Logger	log				= LoggerFactory.getLogger(SwitchNorthboundAPITests.class);

	private final static String	TEST_NODES_FILE	= "/serialization/test-nodes.xml";

	@Rule
	public WireMockRule			wireMockRule	= new WireMockRule(8080);

	@Test
	public void testGetSwitches() {
		ISwitchNorthboundAPI client = getClient("http://localhost:8080/", "admin", "admin");
		Nodes obtainedNodes = client.getNodes("default");
		log.info("Obtained Nodes:\n" + obtainedNodes);

		Nodes expectedNodes = NodesSerializationTests.generateSampleNodes();
		log.info("Expected Nodes:\n" + expectedNodes);

		Assert.assertEquals("Deserialized nodes must be equals to the sample one.", expectedNodes, obtainedNodes);
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
