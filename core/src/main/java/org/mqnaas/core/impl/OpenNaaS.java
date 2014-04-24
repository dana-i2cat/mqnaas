package org.mqnaas.core.impl;

import org.mqnaas.core.api.Specification;

public class OpenNaaS extends AbstractResource {

	public OpenNaaS() {
		super(new Specification(Type.CORE, "OpenNaaS", null));
	}

}
