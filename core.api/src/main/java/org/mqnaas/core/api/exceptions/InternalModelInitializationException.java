package org.mqnaas.core.api.exceptions;

import org.mqnaas.core.api.IApplication;

/**
 * <p>
 * Exception to be thrown by services initializing the internal model of a {@link IApplication}.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class InternalModelInitializationException extends Exception {

	private static final long	serialVersionUID	= 1902148520708811010L;

	public InternalModelInitializationException() {
		super();
	}

	public InternalModelInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalModelInitializationException(String message) {
		super(message);
	}

	public InternalModelInitializationException(Throwable cause) {
		super(cause);
	}

}
