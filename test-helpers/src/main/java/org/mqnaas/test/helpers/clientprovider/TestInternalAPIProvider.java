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

	@SuppressWarnings("unchecked")
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c) {
		if (apiClass.equals(EmptyClientAPI.class)) {
			return (API) new EmptyClientAPI() {
			};
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, EmptyClientConfiguration configuration) {
		if (apiClass.equals(EmptyClientAPI.class)) {
			return (API) new EmptyClientAPI() {
			};
		}
		return null;
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