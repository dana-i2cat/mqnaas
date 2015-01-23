package org.mqnaas.test.helpers.clientprovider;

import org.mqnaas.core.api.credentials.Credentials;

/**
 * Empty client to be used for test purposes.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class EmptyClient {

	private Credentials	credentials;

	// package-private constructor
	EmptyClient() {
	}

	public EmptyClient(Credentials c) {
		this.credentials = c;
	}

	public Credentials getCredentials() {
		return credentials;
	}
}
