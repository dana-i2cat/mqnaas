package org.mqnaas.core.impl.dependencies.samples;

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
