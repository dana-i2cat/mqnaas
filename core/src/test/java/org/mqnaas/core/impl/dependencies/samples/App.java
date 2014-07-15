package org.mqnaas.core.impl.dependencies.samples;

public class App implements IApp {

	private int	activateCount	= 0;
	private int	deactivateCount	= 0;

	@Override
	public void activate() {
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
