package org.mqnaas.core.impl;

import org.mqnaas.core.api.ICoreProvider;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Capability providing access to MQNaaS core resource.
 * </p>
 * <p>
 * It's bound to the core resource.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CoreProvider implements ICoreProvider {

	private static final Logger	log	= LoggerFactory.getLogger(CoreProvider.class);

	@Resource
	IRootResource				coreResource;

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType().equals(Type.CORE);
	}

	@Override
	public void activate() {
		log.info("CoreProvider activated.");
	}

	@Override
	public void deactivate() {
		log.info("CoreProvider deactivated.");
	}

	@Override
	public IRootResource getCore() {
		return coreResource;
	}

}
