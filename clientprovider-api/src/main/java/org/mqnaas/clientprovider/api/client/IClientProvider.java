package org.mqnaas.clientprovider.api.client;

import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.core.api.IResource;

public interface IClientProvider<T, CC> {

	T getClient(IResource resource) throws EndpointNotFoundException;

	T getClient(IResource resource, CC clientConfiguration) throws EndpointNotFoundException;

}
