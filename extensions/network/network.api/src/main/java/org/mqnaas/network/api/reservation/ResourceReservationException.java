package org.mqnaas.network.api.reservation;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ResourceReservationException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8077229848023802861L;

	public ResourceReservationException() {
		super();
	}

	public ResourceReservationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceReservationException(String message) {
		super(message);
	}

	public ResourceReservationException(Throwable cause) {
		super(cause);
	}

}
