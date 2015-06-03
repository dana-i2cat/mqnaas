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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.user.IResourceAssignation;
import org.mqnaas.core.api.user.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 *
 */
public class ResourceAssignment implements IResourceAssignation {

	private static final Logger	LOG	= LoggerFactory.getLogger(ResourceAssignment.class);

	@Resource
	IResource					resource;

	/**
	 * Map containing the assignation between resources ids and usernames. Key=resourceId, Value=username
	 */
	Map<String, String>			resourcesAssignations;

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType().equals(Specification.Type.CORE);
	}

	@Override
	public void activate() throws ApplicationActivationException {
		LOG.info("Initializing ResourceAssignment capability for resource " + resource.getId());
		resourcesAssignations = new HashMap<String, String>();
		LOG.info("Initialized ResourceAssignment capability for resource " + resource.getId());
	}

	@Override
	public void deactivate() {
		LOG.info("Removing ResourceAssignment capability from resource " + resource.getId());
		resourcesAssignations.clear();
		LOG.info("Removed ResourceAssignment capability from resource " + resource.getId());
	}

	@Override
	public Resources getByUser(String username) {
		LOG.info("Getting resources assigned to user " + username);

		List<String> resources = new ArrayList<String>();

		for (String resource : resourcesAssignations.keySet())
			if (resourcesAssignations.get(resource).equals(username))
				resources.add(resource);

		return new Resources(resources);
	}

	@Override
	public void assign(String resourceId, String username) throws IllegalStateException {
		LOG.info("Assigning resource to user: [resourceId=" + resourceId, "username=" + username + "]");

		if (resourcesAssignations.containsKey(resourceId))
			throw new IllegalStateException("Resource " + resourceId + " is already assigned to this or another user. Please unassign it first.");

		resourcesAssignations.put(resourceId, username);

		LOG.info("Resource successfully assigned 	to user: [resourceId=" + resourceId, "username=" + username + "]");

	}

	@Override
	public void unassign(String resourceId, String username) {
		LOG.info("Unassigning resource from user: [resourceId=" + resourceId, "username=" + username + "]");
		if (!resourcesAssignations.containsKey(resourceId) || !resourcesAssignations.get(resourceId).equals(username))
			throw new IllegalStateException(
					"Resource " + resourceId + " either is not assigned to this user or it has been not assigned to any user yet.");

		resourcesAssignations.remove(resourceId);

		LOG.info("Resource successfully unassigned from user: [resourceId=" + resourceId, "username=" + username + "]");

	}

}
