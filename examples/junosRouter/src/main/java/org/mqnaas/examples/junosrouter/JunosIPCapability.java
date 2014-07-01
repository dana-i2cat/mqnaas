package org.mqnaas.examples.junosrouter;

import org.mqnaas.core.api.IResource;
import org.mqnaas.examples.api.router.IIPCapability;
import org.mqnaas.examples.api.router.IPProtocolEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JunosIPCapability implements IIPCapability {

	private static final Logger	log	= LoggerFactory.getLogger(JunosIPCapability.class);

	public static boolean isSupporting(IResource resource) {
		return resource instanceof JunosInterface;
	}

	@Override
	public void setIP(IPProtocolEndpoint iPProtocolEndpoint) {
		log.info(getClass().getName() + " executing setIP(" + iPProtocolEndpoint + ") on resource ?");
	}

	@Override
	public void addIP(IPProtocolEndpoint iPProtocolEndpoint) {
		log.info(getClass().getName() + " executing addIP(" + iPProtocolEndpoint + ") on resource ?");
	}

	@Override
	public void removeIP(IPProtocolEndpoint iPProtocolEndpoint) {
		log.info(getClass().getName() + " executing removeIP(" + iPProtocolEndpoint + ") resource ");
	}

}
