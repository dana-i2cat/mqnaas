package org.mqnaas.extensions.odl.model.topology;

/*
 * #%L
 * MQNaaS :: ODL Model
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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.extensions.odl.hellium.switchmanager.model.Node;
import org.mqnaas.extensions.odl.hellium.switchmanager.model.Node.NodeType;
import org.mqnaas.extensions.odl.hellium.switchmanager.model.NodeConnector;
import org.mqnaas.extensions.odl.hellium.switchmanager.model.NodeConnector.NodeConnectorType;
import org.mqnaas.extensions.odl.hellium.switchmanager.model.PropertyValue;
import org.mqnaas.extensions.odl.hellium.topology.model.Edge;
import org.mqnaas.extensions.odl.hellium.topology.model.EdgeProperty;
import org.mqnaas.extensions.odl.hellium.topology.model.Topology;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.xml.sax.SAXException;

/**
 * <p>
 * Unitary tests checking the serialization/deserialization of the {@link Topology} class.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class TopologySerializationTest {

	private final static String	TEST_TOPOLOGY_FILE	= "/serialization/test-topology.xml";

	private final static String	NODE_ID_1			= "00:00:00:00:00:00:00:01";
	private final static String	NODE_ID_2			= "00:00:00:00:00:00:00:03";

	private final static String	NODE_1_PORT_ID		= "2";
	private final static String	NODE_2_PORT_ID		= "3";

	private final static String	FIRST_LINK_NAME		= "s3-eth3";
	private final static String	SECOND_LINK_NAME	= "s1-eth2";

	private final static String	STATE				= "1";
	private final static String	CONFIG				= "1";
	private static final String	BANDWIDTH			= "100";
	private static final String	TIMESTAMP_VALUE		= "1424856065071";
	private static final String	TIMESTAMP_NAME		= "creation";

	@Test
	public void serializationTest() throws JAXBException, SAXException, IOException {
		Topology topology = generateSampleTopology();

		String serializedXml = SerializationUtils.toXml(topology);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(TEST_TOPOLOGY_FILE));

		// ignore spaces
		XMLUnit.setIgnoreWhitespace(true);

		Diff diff = new Diff(expectedXml, serializedXml);
		XMLAssert.assertXMLEqual("Serialized XML must be equal to the expected one.", diff, false);

	}

	@Test
	public void deserializationTest() throws IOException, JAXBException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(TEST_TOPOLOGY_FILE));
		Topology deserializedTopology = SerializationUtils.fromXml(xml, Topology.class);

		Topology expectedTopology = generateSampleTopology();

		Assert.assertEquals("Deserialized Topology must be equals to the sample one.", expectedTopology, deserializedTopology);

	}

	private Topology generateSampleTopology() {

		EdgeProperty edgeProperty1 = generateEdgeProperty(NODE_ID_1, NODE_1_PORT_ID, NODE_ID_2, NODE_2_PORT_ID, FIRST_LINK_NAME);
		EdgeProperty edgeProperty2 = generateEdgeProperty(NODE_ID_2, NODE_2_PORT_ID, NODE_ID_1, NODE_1_PORT_ID, SECOND_LINK_NAME);

		Topology topology = new Topology();
		topology.setEdgeProperties(Arrays.asList(edgeProperty1, edgeProperty2));

		return topology;
	}

	private EdgeProperty generateEdgeProperty(String tailNode, String tailPort, String headNode, String headPort, String linkName) {

		Edge edge = generateEdge(tailNode, tailPort, headNode, headPort);
		Map<String, PropertyValue> properties = generateProperties(linkName);

		EdgeProperty edgeProperty = new EdgeProperty();
		edgeProperty.setEdge(edge);
		edgeProperty.setProperties(properties);

		return edgeProperty;
	}

	private Map<String, PropertyValue> generateProperties(String linkName) {
		Map<String, PropertyValue> properties = new HashMap<String, PropertyValue>();

		properties.put("state", new PropertyValue(STATE, null));
		properties.put("config", new PropertyValue(CONFIG, null));
		properties.put("name", new PropertyValue(linkName, null));
		properties.put("bandwidth", new PropertyValue(BANDWIDTH, null));
		properties.put("timeStamp", new PropertyValue(TIMESTAMP_VALUE, TIMESTAMP_NAME));

		return properties;
	}

	private Edge generateEdge(String tailNode, String tailPort, String headNode, String headPort) {

		NodeConnector tailConnector = generateNodeConnector(tailNode, tailPort);
		NodeConnector headConnector = generateNodeConnector(headNode, headPort);

		Edge edge = new Edge(headConnector, tailConnector);

		return edge;

	}

	private NodeConnector generateNodeConnector(String nodeId, String port) {
		Node node = generateNode(nodeId, NodeType.OF);

		NodeConnector nodeConnector = new NodeConnector();
		nodeConnector.setNode(node);
		nodeConnector.setNodeConnectorID(port);
		nodeConnector.setNodeConnectorType(NodeConnectorType.OF);

		return nodeConnector;
	}

	private Node generateNode(String nodeId, NodeType type) {

		Node node = new Node();
		node.setNodeID(nodeId);
		node.setNodeType(type);

		return node;
	}
}
