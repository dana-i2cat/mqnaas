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

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;

import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

/**
 * This capability provides access to the {@link IRootResource}s managed by the core or a network. Is bound to the core, and to physical and virtual
 * networks as well as testbeds like NITOS. When used with static networks, e.g. that do not have a administration capability, it's internal state has
 * to initialized from an external logic.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public interface IRootResourceProvider extends ICapability {

	/**
	 * Returns all {@link IRootResource}s currently managed by this capability.
	 * 
	 * @return The list of {@link IRootResource}s managed by the capability.
	 */
	@GET
	@ListsResources
	List<IRootResource> getRootResources();

	/**
	 * Returns the subset of {@link IRootResource}s managed by this capability matching a specific {@link Specification.Type}, model and version.
	 * 
	 * @param type
	 *            Resource type.
	 * @param model
	 *            Resource model.
	 * @param version
	 *            Resource version
	 * @return
	 * @throws ResourceNotFoundException
	 */
	@ListsResources
	List<IRootResource> getRootResources(Specification.Type type, String model, String version) throws ResourceNotFoundException;

	/**
	 * Returns a specific {@link IRootResource} identified by the given id.
	 * 
	 * @param id
	 *            Id of the IRootResource.
	 * @return IRootResource identified by given <code>id</code>
	 * @throws ResourceNotFoundException
	 *             If there's no RootResource managed by this capability instance which such id.
	 */
	IRootResource getRootResource(String id) throws ResourceNotFoundException;

	/**
	 * Initialize the set of resources managed by this capability.
	 * 
	 * @param rootResources
	 *            {@link Collection} of resources managed by this capability.
	 */
	void setRootResources(Collection<IRootResource> rootResources);

}