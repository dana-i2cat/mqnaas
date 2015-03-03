package org.mqnaas.extensions.odl.client.switchnorthbound.api;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.mqnaas.extensions.odl.client.switchnorthbound.api.adapter.PropertiesMapAdapter;

/**
 * Node Properties composed by a {@link Node} and an arbitrary set of properties.
 * 
 * @author Julio Carlos Barrera
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class NodeProperties {

	@XmlElement
	private Node						node;

	@XmlElement
	@XmlJavaTypeAdapter(PropertiesMapAdapter.class)
	private Map<String, PropertyValue>	properties;

	public NodeProperties() {
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
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
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		NodeProperties other = (NodeProperties) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
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
		return "NodeProperties [node=" + node + ", properties=" + properties + "]";
	}

}