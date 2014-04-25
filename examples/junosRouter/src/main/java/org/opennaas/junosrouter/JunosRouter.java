package org.opennaas.junosrouter;

import org.mqnaas.core.api.Specification;
import org.mqnaas.core.impl.AbstractResource;

public class JunosRouter extends AbstractResource {

	public static final String	MODEL	= "Junos";

	public JunosRouter() {
		super(new Specification(Type.ROUTER, MODEL, null));
	}

}
