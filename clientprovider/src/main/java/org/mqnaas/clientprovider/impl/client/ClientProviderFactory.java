package org.mqnaas.clientprovider.impl.client;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.clientprovider.api.client.IClientProvider;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.ICoreModelCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProviderFactory extends AbstractProviderFactory implements IClientProviderFactory {

	private static final Logger		log	= LoggerFactory.getLogger(ClientProviderFactory.class);

	// internal {@link IClassListener} instance
	private InternalClassListener	internalClassListener;

	@DependingOn
	private ICoreModelCapability	coreModelCapability;

	@DependingOn
	private IBundleGuard			bundleGuard;

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType() == Specification.Type.CORE;
	}

	private Map<Class<? extends IInternalClientProvider<?, ?>>, IInternalClientProvider<?, ?>>	internalClientProviders;

	public ClientProviderFactory() {
		// Map of available IInternalClientProvider must be maintained by a class listener
		internalClientProviders = Collections
				.synchronizedMap(new HashMap<Class<? extends IInternalClientProvider<?, ?>>, IInternalClientProvider<?, ?>>());
	}

	private static Set<Type>	VALID_CLIENT_PROVIDERS;

	static {
		VALID_CLIENT_PROVIDERS = new HashSet<Type>();
		VALID_CLIENT_PROVIDERS.add(IClientProvider.class);
		VALID_CLIENT_PROVIDERS.add(IInternalClientProvider.class);
	}

	@Override
	public <T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass) {
		log.info("ClientProvider request received for class: " + clientProviderClass.getCanonicalName());

		// Match against list of providers...
		for (Class<?> internalClientProviderClass : internalClientProviders.keySet()) {
			IInternalClientProvider<?, ?> internalClientProvider = internalClientProviders.get(internalClientProviderClass);

			if (doTypeArgumentsMatch(VALID_CLIENT_PROVIDERS, clientProviderClass, internalClientProviderClass, 2)) {
				// internalClientProvider must be parametrized with <T, CC>
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

	@Override
	public void activate() {
		// register class listeners
		log.info("Registering as ClassListener with IInternalClientProviderClassFilter");
		internalClassListener = new InternalClassListener();
		bundleGuard.registerClassListener(new IInternalClientProviderClassFilter(), internalClassListener);
	}

	@Override
	public void deactivate() {
		// unregister class listeners
		log.info("Unregistering as ClassListener.");
		bundleGuard.unregisterClassListener(internalClassListener);
	}

	private void internalClientProviderAdded(Class<? extends IInternalClientProvider<?, ?>> clazz) {
		try {
			internalClientProviders.put(clazz, (IInternalClientProvider<?, ?>) clazz.newInstance());
		} catch (InstantiationException e) {
			// this are guaranteed to be an instantiable class
			log.error("Error instantiating IClientProvider of class: " + clazz, e);
		} catch (IllegalAccessException e) {
			// at this moment, access to class should be granted
			log.error("Error instantiating IClientProvider of class: " + clazz, e);
		}
	}

	private void internalClientProviderRemoved(Class<? extends IInternalClientProvider<?, ?>> clazz) {
		// remove them
		internalClientProviders.remove(clazz);
	}

	private class InternalClassListener implements IClassListener {

		@Override
		// safe checking castings
		@SuppressWarnings("unchecked")
		public void classEntered(Class<?> clazz) {
			log.debug("Received classEntered event for class: " + clazz.getCanonicalName());
			if (IInternalClientProvider.class.isAssignableFrom(clazz)) {
				internalClientProviderAdded((Class<? extends IInternalClientProvider<?, ?>>) clazz);
			} else {
				log.error("Unknown ClassListener classEntered event received from class " + clazz.getCanonicalName());
			}
		}

		@Override
		// safe checking castings
		@SuppressWarnings("unchecked")
		public void classLeft(Class<?> clazz) {
			log.debug("Received classLeft event for class: " + clazz.getCanonicalName());
			if (IInternalClientProvider.class.isAssignableFrom(clazz)) {
				internalClientProviderRemoved((Class<? extends IInternalClientProvider<?, ?>>) clazz);
			} else {
				log.error("Unknown ClassListener classLeft event received from class " + clazz.getCanonicalName());
			}

		}

	}

	// //////////////////////////////////////////////////////////////////////
	// {@link IInternalClientProvider} {@link IClassFilter} implementation //
	// //////////////////////////////////////////////////////////////////////
	private class IInternalClientProviderClassFilter implements IClassFilter {

		@Override
		public boolean filter(Class<?> clazz) {
			// retrieve only instantiable classes
			return IInternalClientProvider.class.isAssignableFrom(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
		}

	}

}
