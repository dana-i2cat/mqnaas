package org.mqnaas.network.api.topology.device;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.Specification.Type;

/**
 * Allows the administration of a {@link Device}. To be bound to a {@link Device}.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IDeviceAdministration extends ICapability {

	void setType(Type type);
	Type getType();
	
}
