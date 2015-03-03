package org.mqnaas.clientprovider.impl.apiclient;

/*
 * #%L
 * MQNaaS :: Client Provider
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
