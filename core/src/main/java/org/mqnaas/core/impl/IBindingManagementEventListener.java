package org.mqnaas.core.impl;

import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceNode;

/**
 * Defines notifications for changes in the BindingManagement model.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public interface IBindingManagementEventListener {

	/**
	 * Indicates that given ResourceNode has been added to <code>managedBy</code> CapabilityNode.
	 * 
	 * @param added
	 * @param managedBy
	 */
	public void resourceAdded(ResourceNode added, CapabilityNode managedBy);

	/**
	 * Indicates that given ResourceNode has been removed from <code>wasManagedBy</code> CapabilityNode.
	 * 
	 * @param removed
	 * @param wasManagedBy
	 */
	public void resourceRemoved(ResourceNode removed, CapabilityNode wasManagedBy);

	/**
	 * Indicates that given CapabilityNode has been bound to <code>boundTo</code> ResourceNode.
	 * 
	 * @param bound
	 * @param boundTo
	 */
	public void capabilityInstanceBound(CapabilityNode bound, ResourceNode boundTo);

	/**
	 * Indicates that given CapabilityNode has been unbound from <code>wasBoundTo</code> ResourceNode.
	 * 
	 * @param unbound
	 * @param wasBoundTo
	 */
	public void capabilityInstanceUnbound(CapabilityNode unbound, ResourceNode wasBoundTo);

	/**
	 * Indicates that given ApplicationInstance has been added to the system.
	 * 
	 * @param added
	 */
	public void applicationInstanceAdded(ApplicationInstance added);

	/**
	 * /** Indicates that given ApplicationInstance has been removed from the system.
	 * 
	 * @param removed
	 */
	public void applicationInstanceRemoved(ApplicationInstance removed);

	// public void capabilityInstanceActive(CapabilityInstance capabilityInstance);
	//
	// public void capabilityInstanceUnsatisfied(CapabilityInstance capabilityInstance);
	//
	// public void applicationInstanceActive(ApplicationInstance applicationInstance);
	//
	// public void applicationInstanceUnsatisfied(ApplicationInstance applicationInstance);

}
