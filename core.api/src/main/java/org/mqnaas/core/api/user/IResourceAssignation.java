package org.mqnaas.core.api.user;

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

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IRootResource;

/**
 * <p>
 * Capability managing the assignment of resources to specific users.
 * </p>
 * 
 * @author Adrian Rosello Rey (i2CAT)
 *
 */
public interface IResourceAssignation extends ICapability {

	/**
	 * Returns a list of the ids of the {@link IRootResource}s assigned to a specific user.
	 * 
	 * @param username
	 *            Id of a framework's user.
	 * @return Wrapper class providing the ids of the resources assigned to given user.
	 */
	Resources getByUser(String username);

	/**
	 * Assign a specific {@link IRootResource} to a specific user. Only unassigned resources can be assigned to a user.
	 * 
	 * @param resourceId
	 *            Id of the resource to be assigned.
	 * @param username
	 *            Id of the user that will be bound to the resource.
	 * @throws IllegalStateException
	 *             If the resource is already assigned to this or another user.
	 */
	void assign(String resourceId, String username) throws IllegalStateException;

	/**
	 * Unassign a specific {@link IRootResource} from a specific user. A resource can only be unassigned if it's already assigned to that user.
	 * 
	 * @param resourceId
	 *            Id of the resource to be unassigned.
	 * @param username
	 *            Id of the user the resource was bound to.
	 * @throws IllegalStateException
	 *             If given resource is not assigned to specified user, or it has been not assigned to any user yet.
	 */
	void unassign(String resourceId, String username) throws IllegalStateException;

}
