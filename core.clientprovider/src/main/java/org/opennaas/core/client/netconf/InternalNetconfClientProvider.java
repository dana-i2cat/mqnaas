package org.opennaas.core.client.netconf;

import org.opennaas.core.clientprovider.IInternalClientProvider;
import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;

/**
 * This is an example implementation of a programmer who is providing a new client provider
 */
class InternalNetconfClientProvider implements IInternalClientProvider<INetconfClient> {

	@Override
	public INetconfClient getClient(Endpoint ep, Credentials c) {
		// Here the implementer has the Endpoint and the Credentials at her disposal
		return null;
	}

	@Override
	public <C extends INetconfClient> C getClient(Class<C> clazz, Endpoint ep, Credentials c) {
		return null;
	}

}
