package org.opennaas.core.clientprovider.api.client;

public interface IClientProvider<T, CC> {
	
	T getClient();	
	
	T getClient(CC clientConfiguration);

}
