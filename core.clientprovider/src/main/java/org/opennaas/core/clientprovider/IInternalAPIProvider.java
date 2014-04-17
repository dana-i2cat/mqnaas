package org.opennaas.core.clientprovider;

import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;

public interface IInternalAPIProvider<CC> {

	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c);
	
	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration);
	
	<API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, CC configuration, Object applicationSpecificConfiguration);

}
