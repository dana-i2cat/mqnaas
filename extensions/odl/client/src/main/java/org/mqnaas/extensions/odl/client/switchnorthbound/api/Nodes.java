package org.mqnaas.extensions.odl.client.switchnorthbound.api;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * {@link NodeProperties} wrapper.
 * 
 * Based on OpenDaylight Helium release (<a href=
 * "https://github.com/opendaylight/controller/blob/stable/helium/opendaylight/northbound/switchmanager/src/main/java/org/opendaylight/controller/switchmanager/northbound/Nodes.java"
 * >reference</a>).
 * 
 * @author Julio Carlos Barrera
 *
 */
@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.FIELD)
public class Nodes {

	private List<NodeProperties>	nodeProperties;

	public Nodes() {
	}

	public List<NodeProperties> getNodeProperties() {
		return nodeProperties;
	}

	public void setNodeProperties(List<NodeProperties> nodeProperties) {
		this.nodeProperties = nodeProperties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeProperties == null) ? 0 : nodeProperties.hashCode());
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
		Nodes other = (Nodes) obj;
		if (nodeProperties == null) {
			if (other.nodeProperties != null)
				return false;
		} else if (!nodeProperties.equals(other.nodeProperties))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Nodes [nodeProperties=" + nodeProperties + "]";
	}

}
