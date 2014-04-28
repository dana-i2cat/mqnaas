package org.mqnaas.examples.openerRouter;

import org.mqnaas.core.api.Specification;
import org.mqnaas.core.impl.AbstractResource;

public class OpenerRouter extends AbstractResource {

	public static final String	MODEL	= "Opener";

	public OpenerRouter() {
		super(new Specification(Type.ROUTER, MODEL, null));
	}

}
