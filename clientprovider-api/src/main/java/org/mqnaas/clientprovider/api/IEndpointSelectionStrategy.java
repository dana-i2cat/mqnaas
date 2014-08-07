package org.mqnaas.clientprovider.api;

import java.util.Collection;

import org.mqnaas.core.api.Endpoint;

/**
 * Defines an {@link Endpoint} selection strategy based on method {@link #select(String[], Collection)}.
 * 
 * @author Julio Carlos Barrera
 *
 */
public interface IEndpointSelectionStrategy {

	public Endpoint select(String[] protocols, Collection<Endpoint> endpoints);

}
