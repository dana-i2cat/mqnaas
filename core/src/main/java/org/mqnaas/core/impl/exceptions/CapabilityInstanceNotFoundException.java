package org.mqnaas.core.impl.exceptions;

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

import org.mqnaas.core.impl.CapabilityInstance;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class CapabilityInstanceNotFoundException extends Exception {

	/**
	 * Auto-generated UID for serialization
	 */
	private static final long	serialVersionUID	= 6820983405484534893L;

	CapabilityInstance			target;

	public CapabilityInstanceNotFoundException(CapabilityInstance target) {
		super();
		this.target = target;
	}

	public CapabilityInstanceNotFoundException(CapabilityInstance target, String message, Throwable cause) {
		super(message, cause);
		this.target = target;
	}

	public CapabilityInstanceNotFoundException(CapabilityInstance target, String message) {
		super(message);
		this.target = target;
	}

	public CapabilityInstanceNotFoundException(CapabilityInstance target, Throwable cause) {
		super(cause);
		this.target = target;
	}

	public CapabilityInstance getTarget() {
		return target;
	}

}
