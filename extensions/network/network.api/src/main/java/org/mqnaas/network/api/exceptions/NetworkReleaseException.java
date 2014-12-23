package org.mqnaas.network.api.exceptions;

import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;

/**
 * This exception is used to report problems encountered during the network release process of
 * {@link IRequestBasedNetworkManagement#releaseNetwork(org.mqnaas.core.api.IRootResource)}.
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class NetworkReleaseException extends Exception {

	private static final long	serialVersionUID	= -1246124525896177370L;

	public NetworkReleaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkReleaseException(String message) {
		super(message);
	}
}
