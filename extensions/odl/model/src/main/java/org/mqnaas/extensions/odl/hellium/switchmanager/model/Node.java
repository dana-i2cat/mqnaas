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
 * Node, a generic network element.
 * 
 * Based on OpenDaylight Helium release (<a href=
 * "https://github.com/opendaylight/controller/blob/stable/helium/opendaylight/sal/api/src/main/java/org/opendaylight/controller/sal/core/Node.java"
 * >reference</a>).
 * 
 * @author Julio Carlos Barrera
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Node {

	public static enum NodeType {
		/**
		 * Identifier for an OpenFlow node
		 */
		OF,
		/**
		 * Identifier for a PCEP node
		 */
		PE,
		/**
		 * Identifier for a ONEPK node
		 */
		PK,
		/**
		 * Identifier for a node in a non-SDN network
		 */
		PR;
	}

	@XmlElement(name = "id")
	private String		nodeID;

	@XmlElement(name = "type")
	private NodeType	nodeType;

	public Node() {
	}

	public String getNodeID() {
		return nodeID;
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeID == null) ? 0 : nodeID.hashCode());
		result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
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
		Node other = (Node) obj;
		if (nodeID == null) {
			if (other.nodeID != null)
				return false;
		} else if (!nodeID.equals(other.nodeID))
			return false;
		if (nodeType != other.nodeType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Node [nodeID=" + nodeID + ", nodeType=" + nodeType + "]";
	}

}
