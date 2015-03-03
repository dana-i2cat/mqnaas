package org.mqnaas.extensions.odl.client.switchnorthbound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.Node;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.Node.NodeType;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnector;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnector.NodeConnectorType;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnectorProperties;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnectors;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.PropertyValue;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * {@link NodeConnectors} (de)serialization tests
 * 
 * @author Julio Carlos Barrera
 *
 */
public class NodeConnectorsSerializationTests {

	private static final Logger	log							= LoggerFactory.getLogger(NodeConnectorsSerializationTests.class);

	private final static String	TEST_NODE_CONNECTORS_FILE	= "/serialization/test-node-connectors.xml";

	@Test
	public void nodeConnectorsSerializationTest() throws JAXBException, SAXException, IOException {
		NodeConnectors nodes = generateSampleNodeConnectors();

		String serializedXml = SerializationUtils.toXml(nodes);
		log.info("Serialized XML:\n" + serializedXml);

		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(TEST_NODE_CONNECTORS_FILE));
		log.info("Expected XML:\n" + expectedXml);

		// ignore spaces
		XMLUnit.setIgnoreWhitespace(true);

		Diff diff = new Diff(expectedXml, serializedXml);
		XMLAssert.assertXMLEqual("Serialized XML must be equal to the expected one.", diff, false);
	}

	@Test
	public void nodeConnectorsDeserializationTest() throws IOException, JAXBException {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream(TEST_NODE_CONNECTORS_FILE));
		NodeConnectors deserializedNodeConnectors = SerializationUtils.fromXml(xml, NodeConnectors.class);
		log.info("Deserialized Node Connectors:\n" + deserializedNodeConnectors);
		NodeConnectors expectedNodeConnectors = generateSampleNodeConnectors();
		log.info("Expected Node Connectors:\n" + expectedNodeConnectors);

		Assert.assertEquals("Deserialized node connectors must be equals to the sample one.", expectedNodeConnectors, deserializedNodeConnectors);
	}

	static NodeConnectors generateSampleNodeConnectors() {
		NodeConnectors nodeConnectors = new NodeConnectors();
		List<NodeConnectorProperties> nodeConnectorPropertiesList = new ArrayList<NodeConnectorProperties>();

		// node connector 2
		Node node = generateSampleNode("00:00:00:00:00:00:00:01", NodeType.OF);
		NodeConnector nodeConnector = generateSampleNodeConenctor("2", NodeConnectorType.OF, node);
		Map<String, PropertyValue> properties = generateSampleNodeConnectorProperties("1", "1", "10000000000", "s1-eth2");

		NodeConnectorProperties nodeConnectorProperties = new NodeConnectorProperties();
		nodeConnectorProperties.setNodeConnector(nodeConnector);
		nodeConnectorProperties.setProperties(properties);

		nodeConnectorPropertiesList.add(nodeConnectorProperties);

		// node connector 1
		node = generateSampleNode("00:00:00:00:00:00:00:01", NodeType.OF);
		nodeConnector = generateSampleNodeConenctor("1", NodeConnectorType.OF, node);
		properties = generateSampleNodeConnectorProperties("1", "1", "10000000000", "s1-eth1");

		nodeConnectorProperties = new NodeConnectorProperties();
		nodeConnectorProperties.setNodeConnector(nodeConnector);
		nodeConnectorProperties.setProperties(properties);

		nodeConnectorPropertiesList.add(nodeConnectorProperties);

		// node connector 0
		node = generateSampleNode("00:00:00:00:00:00:00:01", NodeType.OF);
		nodeConnector = generateSampleNodeConenctor("0", NodeConnectorType.SW, node);
		properties = generateSampleNodeConnectorProperties("0", "0", null, "s1");

		nodeConnectorProperties = new NodeConnectorProperties();
		nodeConnectorProperties.setNodeConnector(nodeConnector);
		nodeConnectorProperties.setProperties(properties);

		nodeConnectorPropertiesList.add(nodeConnectorProperties);

		nodeConnectors.setNodeConnectorProperties(nodeConnectorPropertiesList);

		return nodeConnectors;
	}

	static Node generateSampleNode(String nodeID, NodeType nodeType) {
		Node node = new Node();
		node.setNodeID(nodeID);
		node.setNodeType(nodeType);
		return node;
	}

	static NodeConnector generateSampleNodeConenctor(String nodeConnectorID, NodeConnectorType nodeConnectorType, Node node) {
		NodeConnector nodeConnector = new NodeConnector();
		nodeConnector.setNodeConnectorID(nodeConnectorID);
		nodeConnector.setNodeConnectorType(nodeConnectorType);
		nodeConnector.setNode(node);
		return nodeConnector;
	}

	private static Map<String, PropertyValue> generateSampleNodeConnectorProperties(String state, String config, String bandwidth, String name) {
		Map<String, PropertyValue> properties = new HashMap<String, PropertyValue>();
		PropertyValue pv = new PropertyValue();

		pv.setValue(state);
		properties.put("state", pv);

		pv = new PropertyValue();
		pv.setValue(config);
		properties.put("config", pv);

		if (bandwidth != null) {
			pv = new PropertyValue();
			pv.setValue(bandwidth);
			properties.put("bandwidth", pv);
		}

		pv = new PropertyValue();
		pv.setValue(name);
		properties.put("name", pv);

		return properties;
	}
}
