package org.mqnaas.core.impl;

import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.impl.exceptions.ApplicationInstanceNotFoundException;
import org.mqnaas.core.impl.exceptions.CapabilityInstanceNotFoundException;
import org.mqnaas.core.impl.resourcetree.ApplicationNode;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceNode;

/**
 * Defines atomic transformations in the BindingManagement model.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public interface IBindingManagement {

	/**
	 * Adds given resource under the control of given ApplicationNode in the bindingManagement model.
	 * 
	 * @param resource
	 * @param managedBy
	 * @throws CapabilityInstanceNotFoundException
	 *             if given CapabilityInstance is not in the bindingManagement model.
	 */
	public void addResourceNode(ResourceNode resource, ApplicationNode managedBy) throws CapabilityInstanceNotFoundException;

	/**
	 * Removes given resource under the control of given ApplicationNode from the bindingManagement model.
	 * 
	 * @param resource
	 * @param managedBy
	 * @throws CapabilityInstanceNotFoundException
	 *             if given CapabilityInstance is not in the bindingManagement model.
	 * @throws ResourceNotFoundException
	 *             if given IResource is not in the bindingManagement model or is not under the control of given CapabilityInstance.
	 */
	public void removeResourceNode(ResourceNode resource, ApplicationNode managedBy) throws CapabilityInstanceNotFoundException,
			ResourceNotFoundException;

	/**
	 * Adds given CapabilityInstance in the bindingManagement model. Given CapabilityInstance is bound to given IResource.
	 * 
	 * @param capabilityInstance
	 * @param toBindTo
	 * @throws ResourceNotFoundException
	 *             if given IResource is not in the bindingManagement model
	 */
	public void bind(CapabilityNode capabilityInstance, ResourceNode toBindTo) throws ResourceNotFoundException;

	/**
	 * Removes given CapabilityInstance from the bindingManagement model. Given CapabilityInstance is unbound from given IResource.
	 * 
	 * @param capabilityInstance
	 * @param boundTo
	 * @throws CapabilityInstanceNotFoundException
	 *             if given CapabilityInstance is not in the bindingManagement model or is not bound to given IResource.
	 * @throws ResourceNotFoundException
	 *             if given IResource is not in the bindingManagement model
	 */
	public void unbind(CapabilityNode capabilityInstance, ResourceNode boundTo) throws CapabilityInstanceNotFoundException,
			ResourceNotFoundException;

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
	public void removeApplicationInstance(ApplicationInstance applicationInstance) throws ApplicationInstanceNotFoundException;

}
