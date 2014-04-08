package org.opennaas.core.api;

/**
 * The <code>Specification</code> contains all the configuration information
 * available about a (physical) device. It is used to describe {@link IRootResource}s.
 */
public class Specification {

	private IRootResource.Type type;

	private String model, version;

	public Specification(IRootResource.Type type, String model, String version) {
		this.type = type;
		this.model = model;
		this.version = version;
	}

	public IRootResource.Type getType() {
		return type;
	}

	public void setType(IRootResource.Type type) {
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
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "Spec [type=" + type + ", model=" + model + ", version="
				+ version + "]";
	}

}
