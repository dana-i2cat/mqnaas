package org.mqnaas.network.impl;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.IUnitAdministration;
import org.mqnaas.core.api.slicing.Range;

public class Unit {

	private IResource			unitResource;
	private IServiceProvider	serviceProvider;

	public Unit(IResource unitResource, IServiceProvider serviceProvider) {
		super();
		this.unitResource = unitResource;
		this.serviceProvider = serviceProvider;
	}

	public IResource getUnit() {
		return unitResource;
	}

	private IUnitAdministration getUnitAdministration() {
		try {
			return serviceProvider.getCapability(unitResource, IUnitAdministration.class);
		} catch (CapabilityNotFoundException c) {
			throw new RuntimeException("Necessary capability not bound to resource " + unitResource, c);
		}
	}

	public Range getRange() {
		return getUnitAdministration().getRange();

	}

	public void setRange(Range range) {
		getUnitAdministration().setRange(range);

	}
}
