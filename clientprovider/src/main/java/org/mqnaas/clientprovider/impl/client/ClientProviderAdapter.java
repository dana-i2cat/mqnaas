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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICoreModelCapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.credentials.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public class ClientProviderAdapter<T, CC> implements InvocationHandler {

	private static final Logger				log	= LoggerFactory.getLogger(ClientProviderAdapter.class);

	private IInternalClientProvider<T, CC>	internalClientProvider;

	private ICoreModelCapability			coreModelCapability;

	private IEndpointSelectionStrategy		endpointSelectionStrategy;

	public ClientProviderAdapter(IInternalClientProvider<T, CC> internalClientProvider, ICoreModelCapability coreModelCapability,
			IEndpointSelectionStrategy endpointSelectionStrategy) {
		this.internalClientProvider = internalClientProvider;
		this.coreModelCapability = coreModelCapability;
		this.endpointSelectionStrategy = endpointSelectionStrategy;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws EndpointNotFoundException {

		IResource resource = (IResource) args[0];
		IRootResource rootResource = coreModelCapability.getRootResource(resource);

		// choose one endpoint using given IEndpointSelectionStrategy
		Endpoint ep = endpointSelectionStrategy.select(internalClientProvider.getProtocols(), rootResource.getDescriptor().getEndpoints());

		if (ep == null) {
			String message = String.format("Unable to find any valid Endpoint from endpoints = %s and protocols = %s.", rootResource.getDescriptor()
					.getEndpoints(),
					internalClientProvider.getProtocols());
			log.error(message);
			throw new EndpointNotFoundException(message);
		}

		Credentials c = rootResource.getDescriptor().getCredentials();

		switch (args == null ? 0 : args.length) {
			case 1:
				return internalClientProvider.getClient(ep, c);
			case 2:
				@SuppressWarnings("unchecked")
				CC clientConfiguration = (CC) args[1];
				return internalClientProvider.getClient(ep, c, clientConfiguration);
		}

		throw new IllegalStateException("Method " + method + " is currently not mapped to the internal client provider implementation");
	}
}