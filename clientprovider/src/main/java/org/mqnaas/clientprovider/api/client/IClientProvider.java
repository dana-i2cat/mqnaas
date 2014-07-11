package org.mqnaas.clientprovider.api.client;

import org.mqnaas.core.api.IResource;

public interface IClientProvider<T, CC> {

	T getClient(IResource resource);

	T getClient(IResource resource, CC clientConfiguration);

}
