package org.mqnaas.clientprovider.api.apiclient;

import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public interface IInternalAPIClientProvider<CC> {

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c);

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration);

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration, Object applicationSpecificConfiguration);

}
