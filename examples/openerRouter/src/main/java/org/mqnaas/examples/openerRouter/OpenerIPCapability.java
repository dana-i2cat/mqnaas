package org.mqnaas.examples.openerRouter;

import org.mqnaas.core.api.IResource;
import org.mqnaas.examples.api.router.IIPCapability;
import org.mqnaas.examples.api.router.IPProtocolEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenerIPCapability implements IIPCapability {

	private static final Logger	log	= LoggerFactory.getLogger(OpenerIPCapability.class);

	public static boolean isSupporting(IResource resource) {
		return resource instanceof OpenerInterface;
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
		log.info(getClass().getName() + " executing removeIP(" + iPProtocolEndpoint + ") on resource ?");
	}

}
