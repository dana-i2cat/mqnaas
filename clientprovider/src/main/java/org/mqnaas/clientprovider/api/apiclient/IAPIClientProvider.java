package org.mqnaas.clientprovider.api.apiclient;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public interface IAPIClientProvider<CC> {

	<T> T getAPIClient(Class<T> apiClass);

	<T> T getAPIClient(Class<T> apiClass, CC clientConfiguration);

	<T, AC> T getAPIClient(Class<T> apiClass, CC clientConfiguration, AC applicationSpecificConfiguration);
}
