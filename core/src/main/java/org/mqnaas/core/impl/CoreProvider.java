package org.mqnaas.core.impl;

/*
 * #%L
 * MQNaaS :: Core
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.mqnaas.core.api.ICoreProvider;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
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
	public void activate() throws ApplicationActivationException {
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

	/**
	 * Checks if a given resource is Core resource.
	 * 
	 * @param resource
	 *            {@link IResource} to be checked
	 * @return true if given resource is Core resource, false otherwise
	 */
	public static boolean isCore(IResource resource) {
		if (IRootResource.class.isAssignableFrom(resource.getClass())) {
			return ((IRootResource) resource).getDescriptor().getSpecification().getType() == Specification.Type.CORE;
		}

		return false;
	}
}
