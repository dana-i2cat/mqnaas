package org.mqnaas.clientprovider.exceptions;

/**
 * Exception thrown when it is not possible to find a valid provider in a provider factory.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ProviderNotFoundException extends Exception {

	private static final long	serialVersionUID	= -5803578880475992806L;

	public ProviderNotFoundException() {
	}

	public ProviderNotFoundException(String message) {
		super(message);
	}

	public ProviderNotFoundException(Throwable cause) {
		super(cause);
	}

	public ProviderNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
