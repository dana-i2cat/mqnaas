package org.mqnaas.clientprovider.api.client;

public interface IClientProvider<T, CC> {

	T getClient();

	T getClient(CC clientConfiguration);

}
