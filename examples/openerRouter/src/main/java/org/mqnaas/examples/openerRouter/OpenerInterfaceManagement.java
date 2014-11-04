package org.mqnaas.examples.openerRouter;

import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.examples.api.router.IInterface;
import org.mqnaas.examples.router.AbstractInterfaceManagement;

public class OpenerInterfaceManagement extends AbstractInterfaceManagement {

	@Override
	public IInterface createInterface(String name) {
		return new OpenerInterface(name);
	}

	public static boolean isSupporting(IRootResource resource) {
		Specification specification = resource.getDescriptor().getSpecification();

		return specification.getType().equals(Type.ROUTER)
				&& specification.getModel().equals("Opener");
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

}
