
package org.mqnaas.core.api.infrastructure;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * Provides access to a network's {@link Infrastructure}. To be bound to a network.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IInfrastructureProvider extends ICapability {

	IResource getInfrastructure();
	
}
