package org.opennaas.router;

import org.opennaas.api.router.IInterface;


public class AbstractInterface implements IInterface {

	private String name;
	
	protected AbstractInterface(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Interface [name=" + getName() + "]";
	}

}
