package org.opennaas.core.clientprovider;

public interface IClientProvider<T, CC> {
	
	T getClient();	
	
	T getClient(CC clientConfiguration);

}
