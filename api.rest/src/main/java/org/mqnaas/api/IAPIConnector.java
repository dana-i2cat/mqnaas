package org.mqnaas.api;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.impl.ApplicationInstance;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceNode;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public interface IAPIConnector extends IApplication {
	
	public void publishCapability(CapabilityNode capabilityNode, ResourceNode boundTo) throws Exception;
	
	public void unpublishCapability(CapabilityNode capabilityNode, ResourceNode boundTo) throws Exception;
	
	public void publishApplication(ApplicationInstance applicationInstance);
	
	public void unpublishApplication(ApplicationInstance applicationInstance);

}
