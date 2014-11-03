package org.mqnaas.clientprovider.exceptions;

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
