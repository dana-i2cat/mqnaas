package org.mqnaas.extensions.odl.helium.topology.model;

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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Class representing the Topology of a network managed by Opendaylight Helium.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Topology {

	@XmlElement(name = "edgeProperties")
	private List<EdgeProperty>	edgeProperties;

	public List<EdgeProperty> getEdgeProperties() {
		return edgeProperties;
	}

	public void setEdgeProperties(List<EdgeProperty> edgeProperties) {
		this.edgeProperties = edgeProperties;
	}

	@Override
	public String toString() {
		return "Topology [edgeProperties=" + edgeProperties + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edgeProperties == null) ? 0 : edgeProperties.hashCode());
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
		Topology other = (Topology) obj;
		if (edgeProperties == null) {
			if (other.edgeProperties != null)
				return false;
		} else if (!edgeProperties.containsAll(other.edgeProperties) && !(other.edgeProperties.containsAll(edgeProperties)))
			return false;
		return true;
	}

}
