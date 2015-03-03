package org.mqnaas.network.api.modelreader;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mqnaas.core.api.IResource;

/**
 * <p>
 * Wrapper class of resources, containing the tree of {@link IResource}s representing the model of a resource.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlRootElement(namespace = "org.mqnaas", name = "resource")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceModelWrapper {

	private String						id;
	private String						name;
	private String						type;

	@XmlElementWrapper(name = "resources")
	@XmlElement(name = "resource")
	private List<ResourceModelWrapper>	resources;

	// constructor withour arguments, required by JAXB
	ResourceModelWrapper() {
	}

	public ResourceModelWrapper(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ResourceModelWrapper> getResources() {

		if (resources == null)
			resources = new ArrayList<ResourceModelWrapper>();

		return resources;
	}

	public void setResources(List<ResourceModelWrapper> resources) {
		this.resources = resources;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
		return "ResourceModelWrapper [id=" + id + ", name=" + name + ", type=" + type + ", resources=" + resources + "]";
	}

}
