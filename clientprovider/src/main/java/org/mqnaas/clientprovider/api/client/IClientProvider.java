package org.mqnaas.clientprovider.api.client;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public interface IClientProvider<T, CC> {

	T getClient();

	T getClient(CC clientConfiguration);

}
