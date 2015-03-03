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

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.impl.exceptions.ApplicationInstanceNotFoundException;
import org.mqnaas.core.impl.resourcetree.ApplicationNode;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceNode;

/**
 * Defines atomic transformations in the BindingManagement model.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public interface IBindingManagement extends ICapability {

	/**
	 * Adds given resource under the control of given ApplicationNode in the bindingManagement model.
	 * 
	 * @param resource
	 * @param managedBy
	 * @param parentInterface
	 * 			  The interface managing given resource in managedBy instance
	 */
	public void addResourceNode(ResourceNode resource, ApplicationNode managedBy, Class<? extends IApplication> parentInterface);

	/**
	 * Removes given resource under the control of given ApplicationNode from the bindingManagement model.
	 * 
	 * @param resource
	 * @param managedBy
	 */
	public void removeResourceNode(ResourceNode resource, ApplicationNode managedBy);

	/**
	 * Adds given CapabilityInstance in the bindingManagement model. Given CapabilityInstance is bound to given IResource.
	 * 
	 * @param capabilityInstance
	 * @param toBindTo
	 */
	public void bind(CapabilityNode capabilityInstance, ResourceNode toBindTo);

	/**
	 * Removes given CapabilityInstance from the bindingManagement model. Given CapabilityInstance is unbound from given IResource.
	 * 
	 * @param capabilityInstance
	 * @param boundTo
	 */
	public void unbind(CapabilityNode capabilityInstance, ResourceNode boundTo);

	/**
	 * Adds given ApplicationInstance in the bindingManagement model.
	 * 
	 * @param applicationInstance
	 */
	public void addApplicationInstance(ApplicationInstance applicationInstance);

	/**
	 * Removes given ApplicationInstance from the bindingManagement model.
	 * 
	 * @param applicationInstance
	 * @throws ApplicationInstanceNotFoundException
	 *             if given ApplicationInstance is not in the bindingManagement model
	 */
	public void removeApplicationInstance(ApplicationInstance applicationInstance);

}
