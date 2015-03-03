package org.mqnaas.core.impl.slicing;

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

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.slicing.IUnitAdministration;
import org.mqnaas.core.api.slicing.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitAdministration implements IUnitAdministration {

	private static final Logger	log	= LoggerFactory.getLogger(UnitAdministration.class);

	@Resource
	IResource					resource;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof UnitResource;
	}

	private Range	range;

	@Override
	public void setRange(Range range) {
		this.range = range;
	}

	@Override
	public Range getRange() {
		return range;
	}

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing UnitAdministration capability for resource " + resource.getId());

		log.info("Initialized UnitAdministration capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing UnitAdministration capability for resource " + resource.getId());

		log.info("Removed UnitAdministration capability for resource " + resource.getId());
	}

}
