package org.mqnaas.test.helpers.resource;

/*
 * #%L
 * MQNaaS :: MQNaaS Test Helpers
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.ITransactionBehavior;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.credentials.Credentials;

/**
 * {@link IResource} factory able to generate artificial resources.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class TestResourceFactory {

	/**
	 * Generates an artificial {@link IRootResource} with given parameters. This resource would be not present in any {@link ICapability}.
	 * 
	 * @param transactionBehavior
	 * @param specification
	 * @param lockingBehaviour
	 * @param endpoints
	 * @return generated resource
	 */
	public static IRootResource createIRootResource(final ITransactionBehavior transactionBehavior, final Specification specification,
			final ILockingBehaviour lockingBehaviour, final Collection<Endpoint> endpoints, final String id, final Credentials credentials) {

		return new IRootResource() {

			RootResourceDescriptor	descriptor	= RootResourceDescriptor.create(specification, endpoints, credentials);

			@Override
			public ITransactionBehavior getTransactionBehaviour() {
				return transactionBehavior;
			}

			@Override
			public ILockingBehaviour getLockingBehaviour() {
				return lockingBehaviour;
			}

			@Override
			public String getId() {
				return id;
			}

			@Override
			public RootResourceDescriptor getDescriptor() {
				return descriptor;
			}
		};
	}

	/**
	 * Generates a {@link Collection} of {@link Endpoint}'s with a single element with a fake {@link URI}.
	 * 
	 * @return a fake collection of endpoints with a single fake element
	 */
	public static Collection<Endpoint> createFakeEndpoints() {
		try {
			return Arrays.asList(new Endpoint(new URI("protocol://server:port/path")));
		} catch (URISyntaxException e) {
			// this must not happen
			return null;
		}
	}
}
