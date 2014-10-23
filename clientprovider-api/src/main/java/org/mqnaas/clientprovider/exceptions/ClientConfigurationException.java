package org.mqnaas.clientprovider.exceptions;

/**
 * Exception thrown when it is not possible to configure a client with specified configuration information.
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ClientConfigurationException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7333791402853543466L;

	public ClientConfigurationException() {
	}

	public ClientConfigurationException(String message) {
		super(message);
	}

	public ClientConfigurationException(Throwable cause) {
		super(cause);
	}

	public ClientConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

}
