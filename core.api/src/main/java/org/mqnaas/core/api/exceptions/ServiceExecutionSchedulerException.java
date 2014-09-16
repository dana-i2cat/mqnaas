package org.mqnaas.core.api.exceptions;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ServiceExecutionSchedulerException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 452740423529946252L;

	public ServiceExecutionSchedulerException() {
		super();
	}

	public ServiceExecutionSchedulerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceExecutionSchedulerException(String message) {
		super(message);
	}

	public ServiceExecutionSchedulerException(Throwable cause) {
		super(cause);
	}

}
