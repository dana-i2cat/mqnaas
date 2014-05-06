package org.mqnaas.core.api.exceptions;

public class ResourceNotFoundException extends Exception {

	private static final long	serialVersionUID	= -2681386586234918505L;

	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}

}
