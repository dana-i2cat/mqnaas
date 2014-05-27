package org.mqnaas.examples.junosrouter;

import org.mqnaas.core.api.IResource;
import org.mqnaas.examples.api.router.IIPCapability;
import org.mqnaas.examples.api.router.IPProtocolEndpoint;

public class JunosIPCapability implements IIPCapability {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof JunosInterface;
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
		System.out.println(getClass().getName() + " executing removeIP(" + iPProtocolEndpoint + ") resource ");
	}

	@Override
	public void onDependenciesResolved() {
		// TODO Auto-generated method stub

	}

}
