package org.mqnaas.bundletree.exceptions;

/**
 * Bundle Not Found Exception
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class BundleNotFoundException extends Exception {

	private static final long	serialVersionUID	= -2583413201025561370L;

	public BundleNotFoundException() {
		super();
	}

	public BundleNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BundleNotFoundException(String message) {
		super(message);
	}

	public BundleNotFoundException(Throwable cause) {
		super(cause);
	}

}
