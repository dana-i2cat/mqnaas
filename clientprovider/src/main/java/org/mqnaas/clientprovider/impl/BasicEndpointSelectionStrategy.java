package org.mqnaas.clientprovider.impl;

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

import java.net.URI;
import java.util.Collection;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.core.api.Endpoint;

/**
 * Basic Endpoint Selection Strategy to be used as default {@link IEndpointSelectionStrategy}. It chooses first {@link Endpoint} matching any of given
 * protocols.
 * 
 * @see #select(String[], Collection)
 * 
 * @author Julio Carlos Barrera
 *
 */
public class BasicEndpointSelectionStrategy implements IEndpointSelectionStrategy {

	/**
	 * Selects first {@link Endpoint} matching any of given protocols using scheme of Endpoint {@link URI} based on {@link URI#getScheme()}.
	 */
	@Override
	public Endpoint select(String[] protocols, Collection<Endpoint> endpoints) {
		for (Endpoint endpoint : endpoints) {
			for (String protocol : protocols) {
				if (endpoint.getUri() != null && endpoint.getUri().getScheme() != null && endpoint.getUri().getScheme().equals(protocol)) {
					return endpoint;
				}
			}
		}
		throw new IllegalArgumentException("No protocol matched against endpoints.");
	}
}
