package org.mqnaas.client.netconf;

import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;

/**
 * This is an example implementation of how to implement a specific client provider
 */
public class InternalNetconfClientProvider implements IInternalClientProvider<NetconfClient, NetconfConfiguration> {

	@Override
	public String[] getProtocols() {
		// Netconf endpoints using SSH as transport layer
		return new String[] { "ssh" };
	}

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
