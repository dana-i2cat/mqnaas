package org.mqnaas.core.impl.slicing;

import org.mqnaas.core.api.slicing.IUnitAdministration;
import org.mqnaas.core.api.slicing.Range;

public class UnitAdministration implements IUnitAdministration {

	private Range range;
	
	@Override
	public void setRange(Range range) {
		this.range = range;
	}

	@Override
	public Range getRange() {
		return range;
	}
	
	@Override
	public void activate() {
		// TODO persistence
	}

	@Override
	public void deactivate() {
	}


}
