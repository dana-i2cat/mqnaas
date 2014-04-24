package org.opennaas.core.clientprovider.api.apiclient;

public interface IAPIClientProvider<CC> {

	<T> T getAPIClient(Class<T> apiClass);

	<T> T getAPIClient(Class<T> apiClass, CC clientConfiguration);

	<T, AC> T getAPIClient(Class<T> apiClass, CC clientConfiguration, AC applicationSpecificConfiguration);
}
