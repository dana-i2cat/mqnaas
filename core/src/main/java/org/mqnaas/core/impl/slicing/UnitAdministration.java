package org.mqnaas.core.impl.slicing;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.slicing.IUnitAdministration;
import org.mqnaas.core.api.slicing.Range;

public class UnitAdministration implements IUnitAdministration {

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
		// TODO persistence
	}

	@Override
	public void deactivate() {
	}

}
