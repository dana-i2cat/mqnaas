package org.mqnaas.network.impl.request;

import org.mqnaas.core.api.IResource;
import org.mqnaas.network.api.request.IRequestAdministration;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.impl.request.RequestResource;

/**
 * Implementation of the {@link IRequestAdministration} capability, which is
 * bound to a {@link RequestResource}.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestAdministration implements IRequestAdministration {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	private Period period;

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
