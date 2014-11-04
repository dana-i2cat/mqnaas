package org.mqnaas.clientprovider.impl.apiclient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.clientprovider.api.apiclient.IInternalAPIClientProvider;
import org.mqnaas.clientprovider.exceptions.ClientConfigurationException;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICoreModelCapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
class APIProviderAdapter<CC> implements InvocationHandler {

	private static final Logger			log	= LoggerFactory.getLogger(APIProviderAdapter.class);

	private IInternalAPIClientProvider<CC>	internalAPIProvider;

	private ICoreModelCapability		coreModelCapability;

	private IEndpointSelectionStrategy	endpointSelectionStrategy;

	public APIProviderAdapter(IInternalAPIClientProvider<CC> internalAPIProvider, ICoreModelCapability coreModelCapability,
			IEndpointSelectionStrategy endpointSelectionStrategy) {
		this.internalAPIProvider = internalAPIProvider;
		this.coreModelCapability = coreModelCapability;
		this.endpointSelectionStrategy = endpointSelectionStrategy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws EndpointNotFoundException, ClientConfigurationException {

		IResource resource = (IResource) args[0];
		IRootResource rootResource = coreModelCapability.getRootResource(resource);

		Class<?> apiClass = (Class<?>) args[1];

		// choose one endpoint using given IEndpointSelectionStrategy
		Endpoint ep = endpointSelectionStrategy.select(internalAPIProvider.getProtocols(), rootResource.getDescriptor().getEndpoints());

		if (ep == null) {
			String message = String.format("Unable to find any valid Endpoint from endpoints = %s and protocols = %s.", rootResource.getDescriptor()
					.getEndpoints(),
					internalAPIProvider.getProtocols());
			log.error(message);
			throw new EndpointNotFoundException(message);
		}

		// TODO Get credentials...
		Credentials c = null;

		switch (args.length) {
			case 2:
				return internalAPIProvider.getClient(apiClass, ep, c);
			case 3:
				// TODO document suppression
				@SuppressWarnings("unchecked")
				CC clientConfiguration1 = (CC) args[2];
				return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration1);
			case 4:
				// TODO document suppression
				@SuppressWarnings("unchecked")
				CC clientConfiguration2 = (CC) args[2];
				Object applicationSpecificConfiguration = args[3];
				return internalAPIProvider.getClient(apiClass, ep, c, clientConfiguration2, applicationSpecificConfiguration);
		}

		throw new IllegalStateException("No mapping for method " + method);
	}
}