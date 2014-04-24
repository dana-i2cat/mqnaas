package org.mqnaas.core.client.netconf;

import org.mqnaas.core.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.core.other.Credentials;
import org.mqnaas.core.other.Endpoint;

/**
 * This is an example implementation of how to implement a specific client provider
 */
public class InternalNetconfClientProvider implements IInternalClientProvider<NetconfClient, NetconfConfiguration> {

	@Override
	public NetconfClient getClient(Endpoint ep, Credentials c) {
		NetconfConfiguration defaultConfiguration = null;
		return getClient(ep, c, defaultConfiguration);
	}

	@Override
	public NetconfClient getClient(Endpoint ep, Credentials c, NetconfConfiguration configuration) {
		return new NetconfClient(ep, c, configuration);
	}

}
