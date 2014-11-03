package org.mqnaas.clientprovider.impl.apiclient;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProvider;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProviderFactory;
import org.mqnaas.clientprovider.api.apiclient.IInternalAPIClientProvider;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.clientprovider.impl.BasicEndpointSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public class APIProviderFactory extends AbstractProviderFactory<IInternalAPIClientProvider<?>> implements IAPIClientProviderFactory {

	private static final Logger	log	= LoggerFactory.getLogger(APIProviderFactory.class);

	private static Set<Type>	VALID_API_PROVIDERS;

	static {
		VALID_API_PROVIDERS = new HashSet<Type>();
		VALID_API_PROVIDERS.add(IAPIClientProvider.class);
		VALID_API_PROVIDERS.add(IInternalAPIClientProvider.class);
	}

	protected Class<?> getInternalProviderClass() {
		return IInternalAPIClientProvider.class;
	}

	@Override
	public <CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass) throws ProviderNotFoundException {
		return getAPIProvider(apiProviderClass, null);
	}

	@Override
	public <CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass, IEndpointSelectionStrategy endpointSelectionStrategy)
			throws ProviderNotFoundException {
		log.info("ClientProvider request received for class: " + apiProviderClass.getCanonicalName());

		// Match against list of providers...
		for (Class<?> internalAPIProviderClass : internalClientProviders.keySet()) {
			@SuppressWarnings("unchecked")
			IInternalAPIClientProvider<CC> internalAPIProvider = (IInternalAPIClientProvider<CC>) internalClientProviders.get(internalAPIProviderClass);

			if (doTypeArgumentsMatch(VALID_API_PROVIDERS, apiProviderClass, internalAPIProviderClass)) {
				// initialize endpointSelectionStrategy if it is null to default one
				if (endpointSelectionStrategy == null) {
					endpointSelectionStrategy = new BasicEndpointSelectionStrategy();
				}

				// internalAPIProvider must be parameterized with <CC>
				@SuppressWarnings("unchecked")
				C c = (C) Proxy.newProxyInstance(apiProviderClass.getClassLoader(), new Class[] { apiProviderClass },
						new APIProviderAdapter<CC>((IInternalAPIClientProvider<CC>) internalAPIProvider, coreModelCapability, endpointSelectionStrategy));

				log.debug("Providing ClientProvider.");
				return c;
			}
		}

		log.warn("Not able to provide APIProvider for class: " + apiProviderClass);
		throw new ProviderNotFoundException("Not able to find a valid API provider for class " + apiProviderClass
				+ ", Endpoint selection strategy " + endpointSelectionStrategy + " and internal providers " + internalClientProviders);
	}

}
