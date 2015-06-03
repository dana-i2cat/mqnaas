package org.mqnaas.extensions.modelreader.api;

/*
 * #%L
 * MQNaaS :: Network API
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mqnaas.core.api.IResource;
import org.mqnaas.extensions.odl.helium.flowprogrammer.model.FlowConfigs;

/**
 * <p>
 * Wrapper class of resources, containing the tree of {@link IResource}s representing the model of a resource.
 * </p>
 * 
 * FIXME It contains specific openflow information. This class should be modified when we implement a generic way to specify the state of a resource.
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlRootElement(namespace = "org.mqnaas", name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceModelWrapper {

	@XmlElement(required = true)
	private String						id;
	@XmlElement(required = true)
	private String						type;

	private Map<String, String>			attributes;

	@XmlElementWrapper(name = "resources")
	@XmlElement(name = "resource")
	private List<ResourceModelWrapper>	resources;

	private FlowConfigs					configuredRules;

	// constructor withour arguments, required by JAXB
	ResourceModelWrapper() {
	}

	public ResourceModelWrapper(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getAttributes() {
		if (attributes == null)
			attributes = new HashMap<String, String>();
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public List<ResourceModelWrapper> getResources() {

		if (resources == null)
			resources = new ArrayList<ResourceModelWrapper>();

		return resources;
	}

	public void setResources(List<ResourceModelWrapper> resources) {
		this.resources = resources;
	}

	public FlowConfigs getConfiguredRules() {
		return configuredRules;
	}

	public void setConfiguredRules(FlowConfigs configuredRules) {
		this.configuredRules = configuredRules;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((configuredRules == null) ? 0 : configuredRules.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((resources == null) ? 0 : resources.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ResourceModelWrapper other = (ResourceModelWrapper) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (configuredRules == null) {
			if (other.configuredRules != null)
				return false;
		} else if (!configuredRules.equals(other.configuredRules))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (resources == null) {
			if (other.resources != null)
				return false;
		} else if (!resources.equals(other.resources))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ResourceModelWrapper [id=" + id + ", type=" + type + ", attributes=" + attributes + ", resources=" + resources + ", configuredRules=" + configuredRules + "]";
	}

}
