package org.mqnaas.clientprovider.impl.client;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mqnaas.client.netconf.InternalNetconfClientProvider;
import org.mqnaas.clientprovider.api.client.IClientProvider;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;

public class ClientProviderFactory extends AbstractProviderFactory implements IClientProviderFactory {

	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Specification.Type.CORE;
	}

	private List<IInternalClientProvider<?, ?>>	internalClientProviders;

	public ClientProviderFactory() {
		// List of available IInternalClientProvider must be maintained by
		// classpath scanning
		internalClientProviders = new ArrayList<IInternalClientProvider<?, ?>>();
		internalClientProviders.add(new InternalNetconfClientProvider());
	}

	private static Set<Type>	VALID_CLIENT_PROVIDERS;

	static {
		VALID_CLIENT_PROVIDERS = new HashSet<Type>();
		VALID_CLIENT_PROVIDERS.add(IClientProvider.class);
		VALID_CLIENT_PROVIDERS.add(IInternalClientProvider.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass) {

		// Match against list of providers...
		for (IInternalClientProvider<?, ?> internalClientProvider : internalClientProviders) {
			Class<?> internalClientProviderClass = internalClientProvider.getClass();

			if (doTypeArgumentsMatch(VALID_CLIENT_PROVIDERS, clientProviderClass, internalClientProviderClass, 2)) {

				C c = (C) Proxy.newProxyInstance(clientProviderClass.getClassLoader(), new Class[] { clientProviderClass },
						new ClientProviderAdapter(internalClientProvider));

				return c;
			}
		}

		return null;
	}

}
