package org.opennaas.junosrouter;

import org.opennaas.api.router.IInterface;
import org.opennaas.core.api.IRootResource;
import org.opennaas.core.api.Specification;
import org.opennaas.router.AbstractInterfaceManagement;

public class JunosInterfaceManagement extends AbstractInterfaceManagement {
	
	@Override
	public IInterface createInterface(String name) {
		return new JunosInterface(name);
	}

	public static boolean isSupporting(IRootResource resource) {
		Specification specification = resource.getSpecification();

		return specification.getType().equals(IRootResource.Type.ROUTER)
				&& specification.getModel().equals(JunosRouter.MODEL);
	}

}
