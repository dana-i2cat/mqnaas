package org.mqnaas.core.api.slicing;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class SlicingException extends Exception {

	private static final long	serialVersionUID	= -9069732478678213253L;

	public SlicingException() {
		super();
	}

	public SlicingException(String message) {
		super(message);
	}

	public SlicingException(Throwable cause) {
		super(cause);
	}

	public SlicingException(String message, Throwable cause) {
		super(message, cause);
	}
}
