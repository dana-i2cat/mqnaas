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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.slicing.IUnitManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of the {@link IUnitManagement} is backed by a {@link CopyOnWriteArrayList} and bound to {@link SliceResource}s.
 * 
 * @author Georg Mansky-Kummert
 */
public class UnitManagment implements IUnitManagement {

	private static final Logger	log	= LoggerFactory.getLogger(UnitManagment.class);

	private List<UnitResource>	units;

	@Resource
	IResource					resource;

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing UnitManagment capability for resource " + resource.getId());
		units = new CopyOnWriteArrayList<UnitResource>();
		log.info("Initialized UnitManagment capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing UnitManagment capability for resource " + resource.getId());
		units = new CopyOnWriteArrayList<UnitResource>();
		log.info("Removed UnitManagment capability for resource " + resource.getId());
	}

	@Override
	public IResource createUnit(String name) {
		if (StringUtils.isEmpty(name))
			throw new IllegalArgumentException("Slice unit require a valid name.");

		for (UnitResource unitResource : units)
			if (unitResource.getName().equals(name))
				throw new IllegalStateException("There already exist a slice unit with name " + name);

		UnitResource unit = new UnitResource(name);
		units.add(unit);
		return unit;
	}

	@Override
	public void removeUnit(IResource unit) {
		units.remove(unit);
	}

	@Override
	public List<IResource> getUnits() {
		return new ArrayList<IResource>(units);
	}

	public static boolean isSupporting(IResource resource) {
		return resource instanceof SliceResource;
	}

}
