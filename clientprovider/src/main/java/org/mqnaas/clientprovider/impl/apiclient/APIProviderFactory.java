package org.mqnaas.clientprovider.impl.apiclient;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.bundletree.utils.ClassFilterFactory;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProvider;
import org.mqnaas.clientprovider.api.apiclient.IAPIProviderFactory;
import org.mqnaas.clientprovider.api.apiclient.IInternalAPIProvider;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APIProviderFactory extends AbstractProviderFactory<IInternalAPIProvider<?>> implements IAPIProviderFactory {

	private static final Logger	log	= LoggerFactory.getLogger(APIProviderFactory.class);

	public APIProviderFactory() {
		super();
		internalClientProviders = new ConcurrentHashMap<Class<IInternalAPIProvider<?>>, IInternalAPIProvider<?>>();
	}

	static {
		VALID_CLIENT_PROVIDERS = new HashSet<Type>();
		VALID_CLIENT_PROVIDERS.add(IAPIClientProvider.class);
		VALID_CLIENT_PROVIDERS.add(IInternalAPIProvider.class);
	}

	@Override
	public void activate() {
		// register class listener
		log.info("Registering as ClassListener.");
		internalClassListener = new InternalClassListener(IInternalAPIProvider.class);
		bundleGuard.registerClassListener(ClassFilterFactory.getBasicClassFilter(IInternalAPIProvider.class), internalClassListener);
	}

	@Override
	public void deactivate() {
		// unregister class listeners
		log.info("Unregistering as ClassListener.");
		bundleGuard.unregisterClassListener(internalClassListener);
	}

	@Override
	public <CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass) {
		log.info("ClientProvider request received for class: " + apiProviderClass.getCanonicalName());

		// Match against list of providers...
		for (Class<?> internalAPIProviderClass : internalClientProviders.keySet()) {
			@SuppressWarnings("unchecked")
			IInternalAPIProvider<CC> internalAPIProvider = (IInternalAPIProvider<CC>) internalClientProviders.get(internalAPIProviderClass);

			if (doTypeArgumentsMatch(VALID_CLIENT_PROVIDERS, apiProviderClass, internalAPIProviderClass)) {
				// internalAPIProvider must be parameterized with <CC>
				@SuppressWarnings("unchecked")
				C c = (C) Proxy.newProxyInstance(apiProviderClass.getClassLoader(), new Class[] { apiProviderClass },
						new APIProviderAdapter<CC>((IInternalAPIProvider<CC>) internalAPIProvider, coreModelCapability));

				log.debug("Providing ClientProvider.");

				return c;
			}
		}

		log.debug("Not able to provide ClientProvider!");

		return null;
	}

}
