package org.mqnaas.clientprovider.impl.client;

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
import org.mqnaas.clientprovider.api.client.IClientProvider;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.clientprovider.impl.BasicEndpointSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProviderFactory extends AbstractProviderFactory<IInternalClientProvider<?, ?>> implements IClientProviderFactory {

	private static final Logger	log	= LoggerFactory.getLogger(ClientProviderFactory.class);

	protected static Set<Type>	VALID_CLIENT_PROVIDERS;

	static {
		VALID_CLIENT_PROVIDERS = new HashSet<Type>();
		VALID_CLIENT_PROVIDERS.add(IClientProvider.class);
		VALID_CLIENT_PROVIDERS.add(IInternalClientProvider.class);
	}

	protected Class<?> getInternalProviderClass() {
		return IInternalClientProvider.class;
	}

	@Override
	public <T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass) throws ProviderNotFoundException {
		return getClientProvider(clientProviderClass, null);
	}

	@Override
	public <T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass,
			IEndpointSelectionStrategy endpointSelectionStrategy) throws ProviderNotFoundException {
		log.info("ClientProvider request received for class: " + clientProviderClass.getCanonicalName());

		// Match against list of providers...
		for (Class<?> internalClientProviderClass : internalClientProviders.keySet()) {
			@SuppressWarnings("unchecked")
			IInternalClientProvider<T, CC> internalClientProvider = (IInternalClientProvider<T, CC>) internalClientProviders
					.get(internalClientProviderClass);

			if (doTypeArgumentsMatch(VALID_CLIENT_PROVIDERS, clientProviderClass, internalClientProviderClass)) {
				// initialize endpointSelectionStrategy if it is null to default one
				if (endpointSelectionStrategy == null) {
					endpointSelectionStrategy = new BasicEndpointSelectionStrategy();
				}

				// internalClientProvider must be parameterized with <T, CC>
				@SuppressWarnings("unchecked")
				C c = (C) Proxy.newProxyInstance(clientProviderClass.getClassLoader(), new Class[] { clientProviderClass },
						new ClientProviderAdapter<T, CC>((IInternalClientProvider<T, CC>) internalClientProvider, coreModelCapability,
								endpointSelectionStrategy));

				log.debug("Providing ClientProvider.");
				return c;
			}
		}

		log.warn("Not able to provide ClientProvider for class: " + clientProviderClass);
		throw new ProviderNotFoundException("Not able to find a valid client provider for class " + clientProviderClass
				+ ", Endpoint selection strategy " + endpointSelectionStrategy + " and internal providers " + internalClientProviders);
	}
}
