package org.mqnaas.clientprovider.impl.client;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.bundletree.utils.ClassFilterFactory;
import org.mqnaas.clientprovider.api.client.IClientProvider;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProviderFactory extends AbstractProviderFactory<IInternalClientProvider<?, ?>> implements IClientProviderFactory {

	private static final Logger	log	= LoggerFactory.getLogger(ClientProviderFactory.class);

	public ClientProviderFactory() {
		super();
		internalClientProviders = new ConcurrentHashMap<Class<IInternalClientProvider<?, ?>>, IInternalClientProvider<?, ?>>();
	}

	static {
		VALID_CLIENT_PROVIDERS = new HashSet<Type>();
		VALID_CLIENT_PROVIDERS.add(IClientProvider.class);
		VALID_CLIENT_PROVIDERS.add(IInternalClientProvider.class);
	}

	@Override
	public void activate() {
		// register class listener
		log.info("Registering as ClassListener.");
		internalClassListener = new InternalClassListener(IInternalClientProvider.class);
		bundleGuard.registerClassListener(ClassFilterFactory.getBasicClassFilter(IInternalClientProvider.class), internalClassListener);
	}

	@Override
	public void deactivate() {
		// unregister class listeners
		log.info("Unregistering as ClassListener.");
		bundleGuard.unregisterClassListener(internalClassListener);
	}

	@Override
	public <T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass) {
		log.info("ClientProvider request received for class: " + clientProviderClass.getCanonicalName());

		// Match against list of providers...
		for (Class<?> internalClientProviderClass : internalClientProviders.keySet()) {
			@SuppressWarnings("unchecked")
			IInternalClientProvider<T, CC> internalClientProvider = (IInternalClientProvider<T, CC>) internalClientProviders
					.get(internalClientProviderClass);

			if (doTypeArgumentsMatch(VALID_CLIENT_PROVIDERS, clientProviderClass, internalClientProviderClass, 2)) {
				// internalClientProvider must be parameterized with <T, CC>
				@SuppressWarnings("unchecked")
				C c = (C) Proxy.newProxyInstance(clientProviderClass.getClassLoader(), new Class[] { clientProviderClass },
						new ClientProviderAdapter<T, CC>((IInternalClientProvider<T, CC>) internalClientProvider, coreModelCapability));

				log.debug("Providing ClientProvider.");

				return c;
			}
		}

		log.debug("Not able to provide ClientProvider!");

		return null;
	}

}
