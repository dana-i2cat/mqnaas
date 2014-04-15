package org.opennaas.junosrouter;

import org.opennaas.core.api.Specification;
import org.opennaas.core.impl.AbstractResource;

public class JunosRouter extends AbstractResource {

	public static final String MODEL = "Junos";
	
	public JunosRouter() {
		super(new Specification(Type.ROUTER, MODEL, null));
	}
	
}
