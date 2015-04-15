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

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.mqnaas.extensions.odl.helium.switchmanager.model.PropertyValue;
import org.mqnaas.extensions.odl.helium.switchmanager.model.adapter.PropertiesMapAdapter;

/**
 * <p>
 * Wrapper class containing the information of an {@link Edge}
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlType(propOrder = { "edge", "properties" })
@XmlAccessorType(XmlAccessType.FIELD)
public class EdgeProperty {

	private Edge						edge;

	@XmlElement
	@XmlJavaTypeAdapter(PropertiesMapAdapter.class)
	private Map<String, PropertyValue>	properties;

	public Edge getEdge() {
		return edge;
	}

	public void setEdge(Edge edge) {
		this.edge = edge;
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
		result = prime * result + ((edge == null) ? 0 : edge.hashCode());
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
		EdgeProperty other = (EdgeProperty) obj;
		if (edge == null) {
			if (other.edge != null)
				return false;
		} else if (!edge.equals(other.edge))
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
		return "EdgeProperty [edge=" + edge + ", properties=" + properties + "]";
	}
}
