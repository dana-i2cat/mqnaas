package org.mqnaas.core.impl.slicing;

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

		return unit.equals(other.unit);
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
