package org.mqnaas.core.api.exceptions;

import org.mqnaas.core.api.IApplication;

/**
 * <p>
 * Application to be thrown by {@link IApplication} during its activation mechanism. Applications should launch this kind of exceptions only if the
 * activation failure compromises the internal state of the application and/or its correct behaviour.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ApplicationActivationException extends Exception {

	private static final long	serialVersionUID	= 8646998997321117086L;

	public ApplicationActivationException() {
		super();
	}

	public ApplicationActivationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationActivationException(String message) {
		super(message);
	}

	public ApplicationActivationException(Throwable cause) {
		super(cause);
	}

}
