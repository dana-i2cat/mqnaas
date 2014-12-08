package org.mqnaas.clientprovider.api.client;

import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public interface IInternalClientProvider<T, CC> {

	String[] getProtocols();

	T getClient(Endpoint ep, Credentials c);

	T getClient(Endpoint ep, Credentials c, CC configuration);

}
