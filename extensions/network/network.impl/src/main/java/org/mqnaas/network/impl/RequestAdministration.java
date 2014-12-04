package org.mqnaas.network.impl;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.network.IRequestAdministration;
import org.mqnaas.core.api.network.Period;

/**
 * Implementation of the {@link IRequestAdministration} capabilities, which is bound to a {@link RequestResource}.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestAdministration implements IRequestAdministration
{

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	private Period	period;

	@Override
	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
	public Period getPeriod() {
		return period;
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

}
