package org.opennaas.core.clientprovider;

import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;


public interface IInternalClientProvider<T extends IClient> {

	T getClient(Endpoint ep, Credentials c);
	
	<C extends T> C getClient(Class<C> clazz, Endpoint ep, Credentials c);

}
