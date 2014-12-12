package org.mqnaas.api.exceptions;

/**
 * An <code>InvalidCapabilityDefinionException</code> is thrown by the API publishing mechanism whenever the given capability interface is violating
 * the restrictions of the publication process.
 * 
 * @author Georg Mansky-Kummert
 */
public class InvalidCapabilityDefinionException extends Exception {

	private static final long	serialVersionUID	= 1L;

	public InvalidCapabilityDefinionException(String message) {
		super(message);
	}

}
