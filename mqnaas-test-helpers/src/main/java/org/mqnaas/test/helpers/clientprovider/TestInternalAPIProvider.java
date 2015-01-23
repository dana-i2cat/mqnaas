package org.mqnaas.test.helpers.clientprovider;

import org.mqnaas.clientprovider.api.apiclient.IInternalAPIClientProvider;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.credentials.Credentials;

/**
 * {@link IInternalAPIClientProvider} for testing purposes able to return only {@link EmptyClientAPI}.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TestInternalAPIProvider implements IInternalAPIClientProvider<EmptyClientConfiguration> {

	@Override
	public String[] getProtocols() {
		return new String[] { "protocol" };
	}

	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c) {
		return getClient(apiClass, ep, c, null, null);
	}

	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, EmptyClientConfiguration configuration) {
		return getClient(apiClass, ep, c, configuration, null);
	}

	@SuppressWarnings("unchecked")
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, EmptyClientConfiguration configuration,
			Object applicationSpecificConfiguration) {
		if (apiClass.equals(EmptyClientAPI.class)) {
			return (API) new EmptyClientAPI() {
			};
		}
		return null;
	}

}