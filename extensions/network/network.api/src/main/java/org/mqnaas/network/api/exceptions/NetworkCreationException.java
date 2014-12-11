package org.mqnaas.network.api.exceptions;

import org.mqnaas.network.api.request.IRequestResourceManagement;

/**
 * This exception is used to report problems encountered during the network creation process of
 * {@link IRequestResourceManagement#createResource(org.mqnaas.core.api.Specification.Type)}.
 * 
 * @author Georg Mansky-Kummert
 */
public class NetworkCreationException extends Exception {

	private static final long	serialVersionUID	= 1L;

	public NetworkCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkCreationException(String message) {
		super(message);
	}

}
