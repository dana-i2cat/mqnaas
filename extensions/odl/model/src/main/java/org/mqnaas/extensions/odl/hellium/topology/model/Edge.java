package org.mqnaas.extensions.odl.hellium.topology.model;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.mqnaas.extensions.odl.hellium.flowprogrammer.model.Node;
import org.mqnaas.extensions.odl.hellium.switchmanager.model.NodeConnector;

/**
 * <p>
 * Class representing an Edge between {@link Node}s of an ODL network.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlType(propOrder = { "tailNodeConnector", "headNodeConnector" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Edge {

	private NodeConnector	headNodeConnector, tailNodeConnector;

	// constructor without arguments, used by JAXB.
	Edge() {
	}

	public Edge(NodeConnector headConnector, NodeConnector tailConnector) {
		this.headNodeConnector = headConnector;
		this.tailNodeConnector = tailConnector;
	}

	public NodeConnector getHeadNodeConnector() {
		return headNodeConnector;
	}

	public void setHeadNodeConnector(NodeConnector headNodeConnector) {
		this.headNodeConnector = headNodeConnector;
	}

	public NodeConnector getTailNodeconnector() {
		return tailNodeConnector;
	}

	public void setTailNodeconnector(NodeConnector tailNodeconnector) {
		this.tailNodeConnector = tailNodeconnector;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((headNodeConnector == null) ? 0 : headNodeConnector.hashCode());
		result = prime * result + ((tailNodeConnector == null) ? 0 : tailNodeConnector.hashCode());
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
		Edge other = (Edge) obj;
		if (headNodeConnector == null) {
			if (other.headNodeConnector != null)
				return false;
		} else if (!headNodeConnector.equals(other.headNodeConnector))
			return false;
		if (tailNodeConnector == null) {
			if (other.tailNodeConnector != null)
				return false;
		} else if (!tailNodeConnector.equals(other.tailNodeConnector))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Edge [headNodeConnector=" + headNodeConnector + ", tailNodeConnector=" + tailNodeConnector + "]";
	}

}
