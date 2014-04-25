package org.opennaas.core.clientprovider.impl.client;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opennaas.core.api.IRootResource;
import org.opennaas.core.client.netconf.InternalNetconfClientProvider;
import org.opennaas.core.clientprovider.api.client.IClientProvider;
import org.opennaas.core.clientprovider.api.client.IClientProviderFactory;
import org.opennaas.core.clientprovider.api.client.IInternalClientProvider;
import org.opennaas.core.clientprovider.impl.AbstractProviderFactory;
import org.opennaas.core.impl.OpenNaaS;

public class ClientProviderFactory extends AbstractProviderFactory implements IClientProviderFactory {

	public static boolean isSupporting(IRootResource resource) {
		return resource instanceof OpenNaaS;
	}
	
	private List<IInternalClientProvider<?, ?>> internalClientProviders;

	public ClientProviderFactory() {
		// List of available IInternalClientProvider must be maintained by
		// classpath scanning
		internalClientProviders = new ArrayList<IInternalClientProvider<?, ?>>();
		internalClientProviders.add(new InternalNetconfClientProvider());
	}
	
	private static Set<Type> VALID_CLIENT_PROVIDERS;

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

			if (doTypeArgumentsMatch(VALID_CLIENT_PROVIDERS,
					clientProviderClass, internalClientProviderClass, 2)) {
				
				C c = (C) Proxy.newProxyInstance(clientProviderClass.getClassLoader(),
						new Class[] { clientProviderClass },
						new ClientProviderAdapter(internalClientProvider));

				return c;
			}
		}

		return null;
	}

}
