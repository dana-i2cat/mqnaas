package org.opennaas.core.clientprovider.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.opennaas.core.clientprovider.api.IInternalAPIProvider;
import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;

class APIProviderAdapter<CC> implements InvocationHandler {

	private IInternalAPIProvider<CC> internalAPIProvider;

	public APIProviderAdapter(IInternalAPIProvider<CC> internalAPIProvider) {
		this.internalAPIProvider = internalAPIProvider;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		Class apiClass = (Class) args[0];
		
		// TODO Get endpoints and credentials...
		Endpoint ep = null;
		Credentials c = null;
		
		switch (args.length) {
		case 1:
			return internalAPIProvider.getClient(apiClass, ep, c);
		case 2:
			CC clientConfiguration1 = (CC) args[1];
			return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration1);
		case 3:
			CC clientConfiguration2 = (CC) args[1];
			Object applicationSpecificConfiguration = args[2];
			return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration2, applicationSpecificConfiguration);
		}
		
		throw new IllegalStateException("No mapping for method " + method);
	}

}