package org.mqnaas.extensions.odl.helium.switchmanager.model;

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
