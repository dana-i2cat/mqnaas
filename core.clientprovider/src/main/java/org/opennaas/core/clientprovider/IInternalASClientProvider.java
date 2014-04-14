package org.opennaas.core.clientprovider;

import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;

public interface IInternalASClientProvider {

	<T> T getClient(Endpoint ep, Credentials c);
	
	<T, C extends T> C getClient(Class<C> clazz, Endpoint ep, Credentials c);

}
