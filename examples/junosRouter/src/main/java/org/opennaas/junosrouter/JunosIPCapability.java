package org.opennaas.junosrouter;

import org.opennaas.api.router.IIPCapability;
import org.opennaas.api.router.IPProtocolEndpoint;
import org.opennaas.core.api.IResource;

public class JunosIPCapability implements IIPCapability {
	
	JunosRouter router;
	
	public static boolean isSupporting(IResource resource) {
		return resource instanceof JunosInterface;
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
		System.out.println(getClass().getName() + " executing removeIP(" + iPProtocolEndpoint + ") resource " + router);
	}

}
