package org.mqnaas.core.api.exceptions;

public class ServiceNotFoundException extends Exception {

	private static final long	serialVersionUID	= -6226616898967244097L;

	public ServiceNotFoundException() {
		super();
	}

	public ServiceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceNotFoundException(String message) {
		super(message);
	}

	public ServiceNotFoundException(Throwable cause) {
		super(cause);
	}

}
