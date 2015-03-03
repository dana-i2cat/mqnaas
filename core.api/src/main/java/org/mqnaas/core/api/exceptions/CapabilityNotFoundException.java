package org.mqnaas.core.api.exceptions;

/*
 * #%L
 * MQNaaS :: Core.API
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

import org.mqnaas.core.api.ICapability;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class CapabilityNotFoundException extends Exception {

	/**
	 * Generated serial version UID
	 */
	private static final long	serialVersionUID	= -2428377962148756255L;

	private ICapability			capability;

	public CapabilityNotFoundException() {
		super();
	}

	public CapabilityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CapabilityNotFoundException(String message) {
		super(message);
	}

	public CapabilityNotFoundException(Throwable cause) {
		super(cause);
	}

	public CapabilityNotFoundException(ICapability capability) {
		super();
		this.setCapability(capability);
	}

	public CapabilityNotFoundException(ICapability capability, String message, Throwable cause) {
		super(message, cause);
		this.setCapability(capability);
	}

	public CapabilityNotFoundException(ICapability capability, String message) {
		super(message);
		this.setCapability(capability);
	}

	public CapabilityNotFoundException(ICapability capability, Throwable cause) {
		super(cause);
		this.setCapability(capability);
	}

	/**
	 * @return the capability
	 */
	public ICapability getCapability() {
		return capability;
	}

	/**
	 * @param capability
	 *            the capability to set
	 */
	public void setCapability(ICapability capability) {
		this.capability = capability;
	}
}
