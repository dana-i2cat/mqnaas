package org.mqnaas.clientprovider.impl.apiclient;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mqnaas.client.cxf.InternalCXFClientProvider;
import org.mqnaas.clientprovider.api.apiclient.IAPClientProviderFactory;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProvider;
import org.mqnaas.clientprovider.api.apiclient.IInternalAPIClientProvider;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public class APIClientProviderFactory extends AbstractProviderFactory implements IAPClientProviderFactory {

	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Specification.Type.CORE;
	}

	private static Set<Type>			VALID_API_PROVIDERS;

	static {
		VALID_API_PROVIDERS = new HashSet<Type>();
		VALID_API_PROVIDERS.add(IAPIClientProvider.class);
		VALID_API_PROVIDERS.add(IInternalAPIClientProvider.class);
	}

	List<IInternalAPIClientProvider<?>>	internalAPIProviders;

	public APIClientProviderFactory() {
		// List of available IInternalAPIProvider must be maintained by
		// classpath scanning
		internalAPIProviders = new ArrayList<IInternalAPIClientProvider<?>>();
		internalAPIProviders.add(new InternalCXFClientProvider());
	}

	@Override
	public <CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass) {
		// Match against list of providers...
		for (IInternalAPIClientProvider<?> internalApiProvider : internalAPIProviders) {
			Class<?> internalAPIProviderClass = internalApiProvider.getClass();

			if (doTypeArgumentsMatch(VALID_API_PROVIDERS, apiProviderClass, internalAPIProviderClass, 1)) {

				C c = (C) Proxy.newProxyInstance(internalAPIProviderClass.getClassLoader(), new Class[] { apiProviderClass },
						new APIClientProviderAdapter(
								internalApiProvider));

				return c;
			}
		}
		return null;
	}

}
