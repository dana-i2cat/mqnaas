package org.mqnaas.core.clientprovider.api.apiclient;

import org.mqnaas.core.other.Credentials;
import org.mqnaas.core.other.Endpoint;

public interface IInternalAPIProvider<CC> {

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c);

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration);

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration, Object applicationSpecificConfiguration);

}
