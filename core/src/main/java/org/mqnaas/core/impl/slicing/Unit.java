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
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.IUnitAdministration;
import org.mqnaas.core.api.slicing.Range;

/**
 * Wrapper to simplify access to the unit's capabilities.
 * 
 * @author Georg Mansky-Kummert
 */
public class Unit {

	private UnitResource		unit;
	private IServiceProvider	serviceProvider;

	public Unit(IResource unit, IServiceProvider serviceProvider) {
		if (!(unit instanceof UnitResource))
			throw new IllegalArgumentException("Given unit must be of type " + UnitResource.class);
		this.unit = (UnitResource) unit;
		this.serviceProvider = serviceProvider;
	}

	private IUnitAdministration getAdministration() {
		try {
			return serviceProvider.getCapability(unit, IUnitAdministration.class);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + unit, e);
		}
	}

	public Range getRange() {
		return getAdministration().getRange();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Unit))
			return false;

		Unit other = (Unit) o;

		return unit.matches(other.unit);
	}

	public void setRange(Range range) {
		getAdministration().setRange(range);
	}

	@Override
	public String toString() {
		return "Unit [name=" + unit.getName() + ", type=" + unit.getType() + ", range=" + getRange() + "]";
	}

	public String getName() {
		return unit.getName();
	}
}
