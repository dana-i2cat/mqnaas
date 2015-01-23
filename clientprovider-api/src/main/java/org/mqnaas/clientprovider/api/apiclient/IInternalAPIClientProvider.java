package org.mqnaas.clientprovider.api.apiclient;

import org.mqnaas.clientprovider.exceptions.ClientConfigurationException;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.credentials.Credentials;

public interface IInternalAPIClientProvider<CC> {

	String[] getProtocols();

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c) throws ClientConfigurationException;

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration) throws ClientConfigurationException;

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration, Object applicationSpecificConfiguration)
			throws ClientConfigurationException;

}
