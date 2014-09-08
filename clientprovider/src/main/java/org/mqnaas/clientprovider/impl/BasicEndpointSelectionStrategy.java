package org.mqnaas.clientprovider.impl;

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
