package org.mqnaas.test.helpers.clientprovider;

import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.credentials.Credentials;

/**
 * {@link IInternalClientProvider} for testing purposes able to return always a new {@link EmptyClient}.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TestInternalClientProvider implements IInternalClientProvider<EmptyClient, EmptyClientConfiguration> {

	@Override
	public String[] getProtocols() {
		return new String[] { "protocol" };
	}

	@Override
	public EmptyClient getClient(Endpoint ep, Credentials c) {
		return new EmptyClient();
	}

	@Override
	public EmptyClient getClient(Endpoint ep, Credentials c, EmptyClientConfiguration configuration) {
		return new EmptyClient();
	}
}