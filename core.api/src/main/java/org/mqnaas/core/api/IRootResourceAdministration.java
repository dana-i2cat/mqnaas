package org.mqnaas.core.api;

/*
 * #%L
 * MQNaaS :: Core.API
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
