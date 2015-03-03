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
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeProperties;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.Nodes;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.PropertyValue;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * {@link Nodes} (de)serialization tests
 * 
 * @author Julio Carlos Barrera
 *
 */
public class NodesSerializationTests {

	private static final Logger	log				= LoggerFactory.getLogger(NodesSerializationTests.class);

	private final static String	TEST_NODES_FILE	= "/serialization/test-nodes.xml";

	@Test
	public void nodesSerializationTest() throws JAXBException, SAXException, IOException {
		Nodes nodes = generateSampleNodes();

		String serializedXml = SerializationUtils.toXml(nodes);
		log.info("Serialized XML:\n" + serializedXml);

		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(TEST_NODES_FILE));
		log.info("Expected XML:\n" + expectedXml);

		// ignore spaces
		XMLUnit.setIgnoreWhitespace(true);

		Diff diff = new Diff(expectedXml, serializedXml);
		XMLAssert.assertXMLEqual("Serialized XML must be equal to the expected one.", diff, false);
	}

	@Test
	public void nodesDeserializationTest() throws IOException, JAXBException {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream(TEST_NODES_FILE));
		Nodes deserializedNodes = SerializationUtils.fromXml(xml, Nodes.class);
		log.info("Deserialized Nodes:\n" + deserializedNodes);
		Nodes expectedNodes = generateSampleNodes();
		log.info("Expected Nodes:\n" + expectedNodes);

		Assert.assertEquals("Deserialized nodes must be equals to the sample one.", expectedNodes, deserializedNodes);
	}

	static Nodes generateSampleNodes() {
		Nodes nodes = new Nodes();
		List<NodeProperties> nodePropertiesList = new ArrayList<NodeProperties>();

		// node 2
		Node node = generateSampleNode("00:00:00:00:00:00:00:02", NodeType.OF);
		Map<String, PropertyValue> properties = generateSampleNodeProperties("-1", "None", "00:00:00:00:00:02", "199", "1424856064717",
				"connectedSince", "256", "0");

		NodeProperties nodeProperties = new NodeProperties();
		nodeProperties.setNode(node);
		nodeProperties.setProperties(properties);

		nodePropertiesList.add(nodeProperties);

		// node 3
		node = generateSampleNode("00:00:00:00:00:00:00:03", NodeType.OF);
		properties = generateSampleNodeProperties("-1", "None", "00:00:00:00:00:03", "199", "1424856064763",
				"connectedSince", "256", "0");

		nodeProperties = new NodeProperties();
		nodeProperties.setNode(node);
		nodeProperties.setProperties(properties);

		nodePropertiesList.add(nodeProperties);

		// node 1
		node = generateSampleNode("00:00:00:00:00:00:00:01", NodeType.OF);
		properties = generateSampleNodeProperties("-1", "None", "00:00:00:00:00:01", "199", "1424856064676",
				"connectedSince", "256", "0");

		nodeProperties = new NodeProperties();
		nodeProperties.setNode(node);
		nodeProperties.setProperties(properties);

		nodePropertiesList.add(nodeProperties);

		nodes.setNodeProperties(nodePropertiesList);

		return nodes;
	}

	static Node generateSampleNode(String nodeID, NodeType nodeType) {
		Node node = new Node();
		node.setNodeID(nodeID);
		node.setNodeType(nodeType);
		return node;
	}

	private static Map<String, PropertyValue> generateSampleNodeProperties(String tables, String description, String macAddress,
			String capabilities, String timeStampsValue, String timeStampName, String buffers, String forwardingMode) {
		Map<String, PropertyValue> properties = new HashMap<String, PropertyValue>();
		PropertyValue pv = new PropertyValue();

		pv.setValue(tables);
		properties.put("tables", pv);

		pv = new PropertyValue();
		pv.setValue(description);
		properties.put("description", pv);

		pv = new PropertyValue();
		pv.setValue(forwardingMode);
		properties.put("forwardingMode", pv);

		pv = new PropertyValue();
		pv.setValue(macAddress);
		properties.put("macAddress", pv);

		pv = new PropertyValue();
		pv.setValue(timeStampsValue);
		pv.setName(timeStampName);
		properties.put("timeStamp", pv);

		pv = new PropertyValue();
		pv.setValue(capabilities);
		properties.put("capabilities", pv);

		pv = new PropertyValue();
		pv.setValue(buffers);
		properties.put("buffers", pv);

		return properties;
	}
}
