package org.mqnaas.clientprovider.impl.apiclient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.mqnaas.clientprovider.api.apiclient.IInternalAPIProvider;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICoreModelCapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;

class APIProviderAdapter<CC> implements InvocationHandler {

	private IInternalAPIProvider<CC>	internalAPIProvider;

	private ICoreModelCapability		coreModelCapability;

	public APIProviderAdapter(IInternalAPIProvider<CC> internalAPIProvider, ICoreModelCapability coreModelCapability) {
		this.internalAPIProvider = internalAPIProvider;
		this.coreModelCapability = coreModelCapability;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		IResource resource = (IResource) args[0];
		IRootResource rootResource = coreModelCapability.getRootResource(resource);

		Class<?> apiClass = (Class<?>) args[1];

		// get first endpoint
		// FIXME think a better strategy for multi-endpoint resources
		Endpoint ep = rootResource.getEndpoints().iterator().next();

		// TODO Get credentials...
		Credentials c = null;

		switch (args.length) {
			case 2:
				return internalAPIProvider.getClient(apiClass, ep, c);
			case 3:
				CC clientConfiguration1 = (CC) args[2];
				return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration1);
			case 4:
				CC clientConfiguration2 = (CC) args[2];
				Object applicationSpecificConfiguration = args[3];
				return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration2, applicationSpecificConfiguration);
		}

		throw new IllegalStateException("No mapping for method " + method);
	}

}