package org.mqnaas.clientprovider.impl.apiclient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.clientprovider.api.apiclient.IInternalAPIProvider;
import org.mqnaas.clientprovider.impl.BasicEndpointSelectionStrategy;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.impl.ICoreModelCapability;

class APIProviderAdapter<CC> implements InvocationHandler {

	private IInternalAPIProvider<CC>	internalAPIProvider;

	private ICoreModelCapability		coreModelCapability;

	private IEndpointSelectionStrategy	endpointSelectionStrategy;

	public APIProviderAdapter(IInternalAPIProvider<CC> internalAPIProvider, ICoreModelCapability coreModelCapability,
			IEndpointSelectionStrategy endpointSelectionStrategy) {
		this.internalAPIProvider = internalAPIProvider;
		this.coreModelCapability = coreModelCapability;
		this.endpointSelectionStrategy = endpointSelectionStrategy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		IResource resource = (IResource) args[0];
		IRootResource rootResource = coreModelCapability.getRootResource(resource);

		Class<?> apiClass = (Class<?>) args[1];

		// choose one endpoint using given IEndpointSelectionStrategy or default BasicEndpointSelectionStrategy
		Endpoint ep = (endpointSelectionStrategy != null) ? endpointSelectionStrategy.select(internalAPIProvider.getProtocols(),
				rootResource.getEndpoints()) : new BasicEndpointSelectionStrategy().select(internalAPIProvider.getProtocols(),
				rootResource.getEndpoints());

		// TODO Get credentials...
		Credentials c = null;

		switch (args.length) {
			case 2:
				return internalAPIProvider.getClient(apiClass, ep, c);
			case 3:
				@SuppressWarnings("unchecked")
				CC clientConfiguration1 = (CC) args[2];
				return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration1);
			case 4:
				@SuppressWarnings("unchecked")
				CC clientConfiguration2 = (CC) args[2];
				Object applicationSpecificConfiguration = args[3];
				return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration2, applicationSpecificConfiguration);
		}

		throw new IllegalStateException("No mapping for method " + method);
	}
}