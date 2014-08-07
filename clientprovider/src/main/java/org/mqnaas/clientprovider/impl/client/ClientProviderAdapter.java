package org.mqnaas.clientprovider.impl.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.clientprovider.impl.BasicEndpointSelectionStrategy;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.impl.ICoreModelCapability;

public class ClientProviderAdapter<T, CC> implements InvocationHandler {

	private IInternalClientProvider<T, CC>	internalClientProvider;

	private ICoreModelCapability			coreModelCapability;

	private IEndpointSelectionStrategy		endpointSelectionStrategy;

	public ClientProviderAdapter(IInternalClientProvider<T, CC> internalClientProvider, ICoreModelCapability coreModelCapability,
			IEndpointSelectionStrategy endpointSelectionStrategy) {
		this.internalClientProvider = internalClientProvider;
		this.coreModelCapability = coreModelCapability;
		this.endpointSelectionStrategy = endpointSelectionStrategy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		IResource resource = (IResource) args[0];
		IRootResource rootResource = coreModelCapability.getRootResource(resource);

		// choose one endpoint using given IEndpointSelectionStrategy or default BasicEndpointSelectionStrategy
		Endpoint ep = (endpointSelectionStrategy != null) ? endpointSelectionStrategy.select(internalClientProvider.getProtocols(),
				rootResource.getEndpoints()) : new BasicEndpointSelectionStrategy().select(internalClientProvider.getProtocols(),
				rootResource.getEndpoints());

		// TODO Get credentials...
		Credentials c = null;

		switch (args == null ? 0 : args.length) {
			case 1:
				return internalClientProvider.getClient(ep, c);
			case 2:
				@SuppressWarnings("unchecked")
				CC clientConfiguration = (CC) args[1];
				return internalClientProvider.getClient(ep, c, clientConfiguration);
		}

		throw new IllegalStateException("Method " + method + " is currently not mapped to the internal client provider implementation");
	}
}