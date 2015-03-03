package org.mqnaas.core.impl;

/*
 * #%L
 * MQNaaS :: Core
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

import org.mqnaas.core.impl.resourcetree.ApplicationNode;
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
	 * Indicates that given ResourceNode has been added to <code>managedBy</code> ApplicationNode.
	 * 
	 * @param added
	 * @param managedBy
	 */
	public void resourceAdded(ResourceNode added, ApplicationNode managedBy);

	/**
	 * Indicates that given ResourceNode has been removed from <code>wasManagedBy</code> ApplicationNode.
	 * 
	 * @param removed
	 * @param wasManagedBy
	 */
	public void resourceRemoved(ResourceNode removed, ApplicationNode wasManagedBy);

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
