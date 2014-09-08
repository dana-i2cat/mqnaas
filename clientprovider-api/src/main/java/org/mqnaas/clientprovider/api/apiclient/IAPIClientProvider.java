package org.mqnaas.clientprovider.api.apiclient;

import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.core.api.IResource;

public interface IAPIClientProvider<CC> {

	<T> T getAPIClient(IResource resource, Class<T> apiClass) throws EndpointNotFoundException;

	<T> T getAPIClient(IResource resource, Class<T> apiClass, CC clientConfiguration) throws EndpointNotFoundException;

	<T, AC> T getAPIClient(IResource resource, Class<T> apiClass, CC clientConfiguration, AC applicationSpecificConfiguration)
			throws EndpointNotFoundException;
}
