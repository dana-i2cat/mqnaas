package org.mqnaas.examples.junosrouter;

import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.examples.api.router.IInterface;
import org.mqnaas.examples.router.AbstractInterfaceManagement;

public class JunosInterfaceManagement extends AbstractInterfaceManagement {

	@Override
	public IInterface createInterface(String name) {
		return new JunosInterface(name);
	}

	public static boolean isSupporting(IRootResource resource) {
		Specification specification = resource.getDescriptor().getSpecification();

		return specification.getType().equals(Specification.Type.ROUTER)
				&& specification.getModel().equals("Junos");
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
