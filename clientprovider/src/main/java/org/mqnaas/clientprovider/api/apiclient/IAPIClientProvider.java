package org.mqnaas.clientprovider.api.apiclient;

import org.mqnaas.core.api.IResource;

public interface IAPIClientProvider<CC> {

	<T> T getAPIClient(IResource resource, Class<T> apiClass);

	<T> T getAPIClient(IResource resource, Class<T> apiClass, CC clientConfiguration);

	<T, AC> T getAPIClient(IResource resource, Class<T> apiClass, CC clientConfiguration, AC applicationSpecificConfiguration);
}
