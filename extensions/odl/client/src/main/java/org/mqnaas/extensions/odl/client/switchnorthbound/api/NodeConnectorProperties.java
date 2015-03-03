package org.mqnaas.extensions.odl.client.switchnorthbound.api;

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

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.mqnaas.extensions.odl.client.switchnorthbound.api.adapter.PropertiesMapAdapter;

/**
 * NodeConnector Properties composed by a {@link NodeConnector} and an arbitrary set of properties.
 * 
 * @author Julio Carlos Barrera
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class NodeConnectorProperties {

	@XmlElement(name = "nodeconnector")
	private NodeConnector				nodeConnector;

	@XmlElement
	@XmlJavaTypeAdapter(PropertiesMapAdapter.class)
	private Map<String, PropertyValue>	properties;

	public NodeConnectorProperties() {
	}

	public NodeConnector getNodeConnector() {
		return nodeConnector;
	}

	public void setNodeConnector(NodeConnector nodeConnector) {
		this.nodeConnector = nodeConnector;
	}

	public Map<String, PropertyValue> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, PropertyValue> properties) {
		this.properties = properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeConnector == null) ? 0 : nodeConnector.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
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
		NodeConnectorProperties other = (NodeConnectorProperties) obj;
		if (nodeConnector == null) {
			if (other.nodeConnector != null)
				return false;
		} else if (!nodeConnector.equals(other.nodeConnector))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NodeConnectorProperties [nodeConnector=" + nodeConnector + ", properties=" + properties + "]";
	}

}
