package org.opennaas.core.clientprovider.api;

import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;


public interface IInternalClientProvider<T, CC> {

	T getClient(Endpoint ep, Credentials c);
	
	T getClient(Endpoint ep, Credentials c, CC configuration);
	
	<C extends T> C getClient(Class<C> clazz, Endpoint ep, Credentials c);
	
	<C extends T> C getClient(Class<C> clazz, Endpoint ep, Credentials c, CC configuration);

}
