package org.mqnaas.core.api;

import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.RemovesResource;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

/**
 * <p>
 * Capability managing the creation and existence of {@link IRootResource}s. It should be bound to Core resource as well as to physical networks.
 * </p>
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface IRootResourceAdministration extends ICapability {

	/**
	 * Creates a {@link IRootResource} instance which features are defined in the given {@link RootResourceDescriptor}. The new resource will be
	 * managed by this capability.
	 * 
	 * @param descriptor
	 *            Definition of the new {@link IRootResource}.
	 * @return A IRoootResource instance with the specifications and behaviours described in the <code>descriptor</code>
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@AddsResource
	IRootResource createRootResource(RootResourceDescriptor descriptor) throws InstantiationException, IllegalAccessException;

	/**
	 * Removes a specific {@link IRootResource} instance from the framework. This <code>resource</code> should have been created by this capability.
	 * 
	 * @param resource
	 *            IRootResource to be removed
	 * @throws ResourceNotFoundException
	 *             If this capability does not manage the given resource.
	 */
	@RemovesResource
	void removeRootResource(IRootResource resource) throws ResourceNotFoundException;

}
