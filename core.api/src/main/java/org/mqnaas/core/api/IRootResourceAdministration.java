package org.mqnaas.core.api;

import java.util.Collection;

import org.mqnaas.core.api.Specification.Type;
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
	 * Creates a {@link IRootResource} instance based on the given {@link Specification} and with the set of specified <code>endpoints</code> The rest
	 * of components of the <code>IRootResource</code> will be set by default by the capability implementation. Please be aware this method is not
	 * annotated with the {@link AddsResource} annotation.
	 * 
	 * @param specification
	 *            Specification containig the {@link Type}, model and version of the resource to be created.
	 * @param endpoints
	 *            Set of endpoints of the resource.
	 * @return A new {@link IRootResource} instance with the given specification and set of endponts.
	 */
	IRootResource createRootResource(Specification specification, Collection<Endpoint> endpoints);

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
