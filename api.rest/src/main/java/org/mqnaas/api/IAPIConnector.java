package org.mqnaas.api;

import org.mqnaas.api.exceptions.InvalidCapabilityDefinionException;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.impl.ApplicationInstance;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public interface IAPIConnector extends IApplication {

	void publish(CapabilityNode capabilityNode) throws InvalidCapabilityDefinionException;

	void unpublish(CapabilityNode capabilityNode);

	void publish(ApplicationInstance applicationInstance);

	void unpublish(ApplicationInstance applicationInstance);

}
