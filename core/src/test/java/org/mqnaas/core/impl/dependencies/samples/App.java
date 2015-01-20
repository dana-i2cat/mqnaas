package org.mqnaas.core.impl.dependencies.samples;

import org.mqnaas.core.api.exceptions.ApplicationActivationException;

public class App implements IApp {

	private int	activateCount	= 0;
	private int	deactivateCount	= 0;

	@Override
	public void activate() throws ApplicationActivationException {
		activateCount++;
	}

	@Override
	public void deactivate() {
		deactivateCount++;

	}

	public boolean activateCalled() {
		return activateCount > 0;
	}

	public boolean deactivateCalled() {
		return deactivateCount > 0;
	}

	@Override
	public boolean isActive() {
		return (activateCount == deactivateCount + 1);
	}

}
