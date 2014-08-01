package org.mqnaas.test.helpers.clientprovider;

import org.mqnaas.clientprovider.api.apiclient.IInternalAPIProvider;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;

/**
 * {@link IInternalAPIProvider} for testing purposes able to return only {@link EmptyClientAPI}.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TestInternalAPIProvider implements IInternalAPIProvider<EmptyClientConfiguration> {

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