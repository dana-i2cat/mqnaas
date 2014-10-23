package org.mqnaas.clientprovider.api.apiclient;

import org.mqnaas.clientprovider.exceptions.ClientConfigurationException;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;

public interface IInternalAPIProvider<CC> {

	String[] getProtocols();

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c) throws ClientConfigurationException;

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration) throws ClientConfigurationException;

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration, Object applicationSpecificConfiguration)
			throws ClientConfigurationException;

}
