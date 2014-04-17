package org.opennaas.core.client.netconf;

import java.lang.reflect.Constructor;

import org.opennaas.core.clientprovider.api.IInternalClientProvider;
import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;

/**
 * This is an example implementation of how to implement a specific client provider
 */
public class InternalNetconfClientProvider implements IInternalClientProvider<NetconfClient, NetconfConfiguration> {

	@Override
	public NetconfClient getClient(Endpoint ep, Credentials c) {
		return getClient(ep, c, null);
	}

	@Override
	public <C extends NetconfClient> C getClient(Class<C> clazz, Endpoint ep, Credentials c) {
		return getClient(clazz, ep, c, null);
	}

	@Override
	public NetconfClient getClient(Endpoint ep, Credentials c, NetconfConfiguration configuration) {
		return new NetconfClient(ep, c, configuration);
	}

	@Override
	public <C extends NetconfClient> C getClient(Class<C> clazz, Endpoint ep, Credentials c, NetconfConfiguration configuration) {
		Constructor<C> clientConstructor;
		try {
			clientConstructor = clazz.getConstructor(Endpoint.class, Credentials.class, NetconfConfiguration.class);
			return clientConstructor.newInstance(ep, c, configuration);
		} catch (Exception e) {
			// TODO Ignore for now
			e.printStackTrace();
		}
		
		throw new IllegalStateException("Client could not be constructed...");
	}

}
