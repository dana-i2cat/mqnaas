package org.mqnaas.core.impl.dependencies.samples;

import org.mqnaas.core.api.IApplication;

public interface IApp extends IApplication {

	public boolean activateCalled();

	public boolean deactivateCalled();

	public boolean isActive();

}
