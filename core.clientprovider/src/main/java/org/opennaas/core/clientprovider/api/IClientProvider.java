package org.opennaas.core.clientprovider.api;

public interface IClientProvider<T, CC> {
	
	T getClient();	
	
	T getClient(CC clientConfiguration);
	
	<C extends T> C getClient(Class<C> clazz);
	
	<C extends T> C getClient(Class<C> clazz, CC clientConfiguration);

}
