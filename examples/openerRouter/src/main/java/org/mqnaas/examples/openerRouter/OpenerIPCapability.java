package org.mqnaas.examples.openerRouter;

import org.mqnaas.core.api.IResource;
import org.mqnaas.examples.api.router.IIPCapability;
import org.mqnaas.examples.api.router.IPProtocolEndpoint;

public class OpenerIPCapability implements IIPCapability {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof OpenerInterface;
	}

	@Override
	public void setIP(IPProtocolEndpoint iPProtocolEndpoint) {
		System.out.println(getClass().getName() + " executing setIP(" + iPProtocolEndpoint + ") on resource ?");
	}

	@Override
	public void addIP(IPProtocolEndpoint iPProtocolEndpoint) {
		System.out.println(getClass().getName() + " executing addIP(" + iPProtocolEndpoint + ") on resource ?");
	}

	@Override
	public void removeIP(IPProtocolEndpoint iPProtocolEndpoint) {
		System.out.println(getClass().getName() + " executing removeIP(" + iPProtocolEndpoint + ") on resource ?");
	}

	@Override
	public void onDependenciesResolved() {
		// TODO Auto-generated method stub

	}

}
