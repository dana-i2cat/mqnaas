package org.mqnaas.core.impl;

import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;

public class MQNaaS extends AbstractResource {

	public MQNaaS() {
		super(new Specification(Type.CORE, "MQNaaS", null));
	}

}
