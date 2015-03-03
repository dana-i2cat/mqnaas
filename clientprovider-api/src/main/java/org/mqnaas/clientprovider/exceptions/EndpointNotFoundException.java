package org.mqnaas.clientprovider.exceptions;

/*
 * #%L
 * MQNaaS :: Client Provider API
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

import org.mqnaas.core.api.Endpoint;

/**
 * Exception thrown when it is not possible to find a valid {@link Endpoint} in a client provider.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class EndpointNotFoundException extends Exception {

	private static final long	serialVersionUID	= -4960102615891899065L;

	public EndpointNotFoundException() {
	}

	public EndpointNotFoundException(String message) {
		super(message);
	}

	public EndpointNotFoundException(Throwable cause) {
		super(cause);
	}

	public EndpointNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
