package org.mqnaas.clientprovider.impl.apiclient;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProvider;
import org.mqnaas.clientprovider.api.apiclient.IAPIProviderFactory;
import org.mqnaas.clientprovider.api.apiclient.IInternalAPIProvider;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.clientprovider.impl.BasicEndpointSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIProviderFactory extends AbstractProviderFactory<IInternalAPIProvider<?>> implements IAPIProviderFactory {

	private static final Logger	log	= LoggerFactory.getLogger(APIProviderFactory.class);

	private static Set<Type>	VALID_API_PROVIDERS;

	static {
		VALID_API_PROVIDERS = new HashSet<Type>();
		VALID_API_PROVIDERS.add(IAPIClientProvider.class);
		VALID_API_PROVIDERS.add(IInternalAPIProvider.class);
	}

	protected Class<?> getInternalProviderClass() {
		return IInternalAPIProvider.class;
	}

	@Override
	public <CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass) {
		return getAPIProvider(apiProviderClass, null);
	}

	@Override
	public <CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass, IEndpointSelectionStrategy endpointSelectionStrategy) {
		log.info("ClientProvider request received for class: " + apiProviderClass.getCanonicalName());

		// Match against list of providers...
		for (Class<?> internalAPIProviderClass : internalClientProviders.keySet()) {
			@SuppressWarnings("unchecked")
			IInternalAPIProvider<CC> internalAPIProvider = (IInternalAPIProvider<CC>) internalClientProviders.get(internalAPIProviderClass);

			if (doTypeArgumentsMatch(VALID_API_PROVIDERS, apiProviderClass, internalAPIProviderClass)) {
				// initialize endpointSelectionStrategy if it is null to default one
				if (endpointSelectionStrategy == null) {
					endpointSelectionStrategy = new BasicEndpointSelectionStrategy();
				}

				// internalAPIProvider must be parameterized with <CC>
				@SuppressWarnings("unchecked")
				C c = (C) Proxy.newProxyInstance(apiProviderClass.getClassLoader(), new Class[] { apiProviderClass },
						new APIProviderAdapter<CC>((IInternalAPIProvider<CC>) internalAPIProvider, coreModelCapability, endpointSelectionStrategy));

				log.debug("Providing ClientProvider.");

				return c;
			}
		}

		log.debug("Not able to provide ClientProvider!");

		return null;
	}

}
