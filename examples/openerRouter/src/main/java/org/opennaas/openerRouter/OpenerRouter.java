package org.opennaas.openerRouter;

import org.opennaas.core.api.Specification;
import org.opennaas.core.impl.AbstractResource;

public class OpenerRouter extends AbstractResource {

	public static final String MODEL = "Opener";
	
	public OpenerRouter() {
		super(new Specification(Type.ROUTER, MODEL, null));
	}

}
