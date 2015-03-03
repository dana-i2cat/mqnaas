package org.mqnaas.extensions.odl.client.switchnorthbound.api;

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
