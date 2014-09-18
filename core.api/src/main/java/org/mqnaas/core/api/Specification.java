package org.mqnaas.core.api;

import javax.xml.bind.annotation.XmlElement;

/**
 * The <code>Specification</code> contains all the configuration information available about a (physical) device. It is used to describe
 * {@link IRootResource}s.
 */
public class Specification {

	/**
	 * The <code>Type</code> of an {@link IRootResource} is part of the technical specification of a device and serves as a basic classification of
	 * resources.
	 */
	public enum Type {
		/**
		 * The platform core device
		 */
		CORE("MQNaaS"),
		/**
		 * A network
		 */
		NETWORK("Network"),
		/**
		 * A router
		 */
		ROUTER("Router"),
		/**
		 * A switch
		 */
		SWITCH("Switch"),
		/**
		 * A Bandwidth on Demand device
		 */
		BoD("BoD"),
		/**
		 * An Openflow Switch
		 */
		OF_SWITCH("OFSwitch"),
		/**
		 * Other devices
		 */

		OTHER("Other");

		private String	name;

		Type(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	};

	@XmlElement(required = true)
	private Type	type;

	private String	model, version;

	public Specification() {

	}

	public Specification(Type type) {
		this(type, null);
	}

	public Specification(Type type, String model) {
		this(type, model, null);
	}

	public Specification(Type type, String model, String version) {
		this.type = type;
		this.model = model;
		this.version = version;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		Specification other = (Specification) obj;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		if (type != other.type)
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Spec [type=" + type + ", model=" + model + ", version="
				+ version + "]";
	}

}
