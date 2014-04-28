package org.mqnaas.examples.openerRouter;

import org.mqnaas.core.api.IResource;
import org.mqnaas.examples.api.router.IIPCapability;
import org.mqnaas.examples.api.router.IPProtocolEndpoint;

public class OpenerIPCapability implements IIPCapability {

	private OpenerRouter	router;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof OpenerInterface;
	}

	@Override
	public void setIP(IPProtocolEndpoint iPProtocolEndpoint) {
		System.out.println(getClass().getName() + " executing setIP(" + iPProtocolEndpoint + ") on resource " + router);
	}

	@Override
	public void addIP(IPProtocolEndpoint iPProtocolEndpoint) {
		System.out.println(getClass().getName() + " executing addIP(" + iPProtocolEndpoint + ") on resource " + router);
	}

	@Override
	public void removeIP(IPProtocolEndpoint iPProtocolEndpoint) {
		System.out.println(getClass().getName() + " executing removeIP(" + iPProtocolEndpoint + ") on resource " + router);
	}

}
