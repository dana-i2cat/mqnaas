package org.mqnaas.extensions.odl.hellium.switchmanager.model;

/*
 * #%L
 * MQNaaS :: ODL Client
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * NodeConnector, a generic network connection.
 * 
 * Based on OpenDaylight Helium release (<a href=
 * "https://github.com/opendaylight/controller/blob/stable/helium/opendaylight/sal/api/src/main/java/org/opendaylight/controller/sal/core/NodeConnector.java"
 * >reference</a>).
 * 
 * @author Julio Carlos Barrera
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class NodeConnector {

	public static enum NodeConnectorType {
		/**
		 * Represents the OFPP_CONTROLLER reserved port to forward a packet to the controller, this is to send data packets to the controller from the
		 * data plane triggering a packet_in event.
		 */
		CTRL,
		/**
		 * Represents the OFPP_ALL reserved OF port to forward to ALL the ports in the system , should be used for flooding like mechanism to be used
		 * cautiously to avoid excessive flooding.
		 */
		ALL,
		/**
		 * Represents the OFPP_LOCAL reserved OF port to access the local networking stack of the node of which the packet is destined. Typically used
		 * for inband OF communications channel.
		 */
		SW,
		/**
		 * Describes OFPP_Normal reserved port destination that invokes the traditional native L2/L3 HW normal forwarding functionality if supported
		 * on the forwarding element.
		 */
		HW,
		OF,
		PE,
		PK,
		O2E,
		E2O,
		O2K,
		K2O,
		E2K,
		K2E,
		PR
	}

	@XmlElement(name = "id")
	private String				nodeConnectorID;

	@XmlElement(name = "type")
	private NodeConnectorType	nodeConnectorType;

	@XmlElement(name = "node")
	private Node				node;

	public NodeConnector() {
	}

	public String getNodeConnectorID() {
		return nodeConnectorID;
	}

	public void setNodeConnectorID(String nodeConnectorID) {
		this.nodeConnectorID = nodeConnectorID;
	}

	public NodeConnectorType getNodeConnectorType() {
		return nodeConnectorType;
	}

	public void setNodeConnectorType(NodeConnectorType nodeConnectorType) {
		this.nodeConnectorType = nodeConnectorType;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + ((nodeConnectorID == null) ? 0 : nodeConnectorID.hashCode());
		result = prime * result + ((nodeConnectorType == null) ? 0 : nodeConnectorType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeConnector other = (NodeConnector) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (nodeConnectorID == null) {
			if (other.nodeConnectorID != null)
				return false;
		} else if (!nodeConnectorID.equals(other.nodeConnectorID))
			return false;
		if (nodeConnectorType != other.nodeConnectorType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NodeConnector [nodeConnectorID=" + nodeConnectorID + ", nodeConnectorType=" + nodeConnectorType + ", node=" + node + "]";
	}

}