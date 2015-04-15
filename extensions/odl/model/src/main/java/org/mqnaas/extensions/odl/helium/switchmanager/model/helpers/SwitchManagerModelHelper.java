package org.mqnaas.extensions.odl.helium.switchmanager.model.helpers;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mqnaas.extensions.odl.helium.switchmanager.model.Node;
import org.mqnaas.extensions.odl.helium.switchmanager.model.NodeConnector;
import org.mqnaas.extensions.odl.helium.switchmanager.model.NodeConnectorProperties;
import org.mqnaas.extensions.odl.helium.switchmanager.model.NodeConnectors;
import org.mqnaas.extensions.odl.helium.switchmanager.model.NodeProperties;
import org.mqnaas.extensions.odl.helium.switchmanager.model.Nodes;
import org.mqnaas.extensions.odl.helium.switchmanager.model.PropertyValue;
import org.mqnaas.extensions.odl.helium.switchmanager.model.Node.NodeType;
import org.mqnaas.extensions.odl.helium.switchmanager.model.NodeConnector.NodeConnectorType;

public class SwitchManagerModelHelper {

	public static Nodes generateSampleNodes() {
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

	public static Node generateSampleNode(String nodeID, NodeType nodeType) {
		Node node = new Node();
		node.setNodeID(nodeID);
		node.setNodeType(nodeType);
		return node;
	}

	public static Map<String, PropertyValue> generateSampleNodeProperties(String tables, String description, String macAddress,
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

	public static NodeConnectors generateSampleNodeConnectors() {
		NodeConnectors nodeConnectors = new NodeConnectors();
		List<NodeConnectorProperties> nodeConnectorPropertiesList = new ArrayList<NodeConnectorProperties>();

		// node connector 2
		Node node = generateSampleNode("00:00:00:00:00:00:00:01", NodeType.OF);
		NodeConnector nodeConnector = generateSampleNodeConnector("2", NodeConnectorType.OF, node);
		Map<String, PropertyValue> properties = generateSampleNodeConnectorProperties("1", "1", "10000000000", "s1-eth2");

		NodeConnectorProperties nodeConnectorProperties = new NodeConnectorProperties();
		nodeConnectorProperties.setNodeConnector(nodeConnector);
		nodeConnectorProperties.setProperties(properties);

		nodeConnectorPropertiesList.add(nodeConnectorProperties);

		// node connector 1
		node = generateSampleNode("00:00:00:00:00:00:00:01", NodeType.OF);
		nodeConnector = generateSampleNodeConnector("1", NodeConnectorType.OF, node);
		properties = generateSampleNodeConnectorProperties("1", "1", "10000000000", "s1-eth1");

		nodeConnectorProperties = new NodeConnectorProperties();
		nodeConnectorProperties.setNodeConnector(nodeConnector);
		nodeConnectorProperties.setProperties(properties);

		nodeConnectorPropertiesList.add(nodeConnectorProperties);

		// node connector 0
		node = generateSampleNode("00:00:00:00:00:00:00:01", NodeType.OF);
		nodeConnector = generateSampleNodeConnector("0", NodeConnectorType.SW, node);
		properties = generateSampleNodeConnectorProperties("0", "0", null, "s1");

		nodeConnectorProperties = new NodeConnectorProperties();
		nodeConnectorProperties.setNodeConnector(nodeConnector);
		nodeConnectorProperties.setProperties(properties);

		nodeConnectorPropertiesList.add(nodeConnectorProperties);

		nodeConnectors.setNodeConnectorProperties(nodeConnectorPropertiesList);

		return nodeConnectors;
	}

	public static NodeConnector generateSampleNodeConnector(String nodeConnectorID, NodeConnectorType nodeConnectorType, Node node) {
		NodeConnector nodeConnector = new NodeConnector();
		nodeConnector.setNodeConnectorID(nodeConnectorID);
		nodeConnector.setNodeConnectorType(nodeConnectorType);
		nodeConnector.setNode(node);
		return nodeConnector;
	}

	public static Map<String, PropertyValue> generateSampleNodeConnectorProperties(String state, String config, String bandwidth, String name) {
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
