package org.opennaas.core.impl;

import org.opennaas.core.api.Specification;

public class OpenNaaS extends AbstractResource {

	public OpenNaaS() {
		super(new Specification(Type.CORE, "OpenNaaS", null));
	}

}
