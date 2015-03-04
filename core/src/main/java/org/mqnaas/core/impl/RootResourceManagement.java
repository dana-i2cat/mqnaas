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
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootResourceManagement implements IRootResourceProvider, IRootResourceAdministration {

	private static final Logger			log			= LoggerFactory.getLogger(RootResourceManagement.class);

	private List<IRootResource>			resources	= new ArrayList<IRootResource>();

	@DependingOn
	private IResourceManagementListener	rmListener;

	@Resource
	private IResource					resource;

	public static boolean isSupporting(IRootResource resource) {
		Specification specification = resource.getDescriptor().getSpecification();

		return specification.getType() == Type.CORE ||
				(specification.getType() == Type.NETWORK && (
				!StringUtils.equals(specification.getModel(), "nitos") &&
						!StringUtils.equals(specification.getModel(), "odl") &&
				!StringUtils.equals(specification.getModel(), "virtual")));
	}

	@Override
	public void setRootResources(Collection<IRootResource> rootResources) {
		log.info("Initializing RootResources.");

		if (resources.isEmpty())
			resources.addAll(rootResources);
		else
			throw new IllegalStateException("Capability resources are already initialized.");
	}

	@Override
	public void removeRootResource(IRootResource resource) {
		log.info("Removing resource " + (resource == null ? null : resource.getId()));
		resources.remove(resource);
	}

	@Override
	public List<IRootResource> getRootResources() {
		log.info("Getting all RootResources");
		return new ArrayList<IRootResource>(resources);
	}

	@Override
	public List<IRootResource> getRootResources(Specification.Type type, String model, String version) {

		log.info("Getting al RootResources with filter [type=" + type + ",model=" + model + ",version=" + version + "]");

		List<IRootResource> filteredResources = new ArrayList<IRootResource>();

		for (IRootResource resource : getRootResources()) {

			Specification specification = resource.getDescriptor().getSpecification();

			boolean matches = true;
			matches &= type != null ? specification.getType().equals(type) : true;
			matches &= model != null ? specification.getModel().equals(model) : true;
			matches &= version != null ? specification.getVersion().equals(version) : true;

			if (matches)
				filteredResources.add(resource);

		}

		log.debug("Found " + filteredResources.size() + " resources matching filter [type=" + type + ",model=" + model + ",version=" + version + "]");

		return filteredResources;
	}

	public IRootResource getRootResource(Specification specification) throws ResourceNotFoundException {
		List<IRootResource> filteredResources = getRootResources(specification);

		if (filteredResources.isEmpty())
			throw new ResourceNotFoundException("No resource found with this specification: " + specification);

		return filteredResources.get(0);
	}

	public List<IRootResource> getRootResources(Specification specification) throws ResourceNotFoundException {

		if (specification == null)
			throw new NullPointerException("Specification can't be null.");

		return getRootResources(specification.getType(), specification.getModel(), specification.getVersion());
	}

	@Override
	public IRootResource createRootResource(RootResourceDescriptor descriptor) throws InstantiationException, IllegalAccessException {

		if (descriptor == null)
			throw new NullPointerException("Descriptor can't be null.");

		if (descriptor.getEndpoints().isEmpty()) {
			if (!descriptor.getSpecification().getType().equals(Type.NETWORK) && !(descriptor.getSpecification().getType().equals(Type.CORE)))
				throw new IllegalArgumentException(
						"Invalid endpoint collection, at least one endpoint is required. Endpoints = " + descriptor.getEndpoints());
		}

		RootResource resource = new RootResource(descriptor);
		resources.add(resource);
		return resource;
	}

	@Override
	public IRootResource getRootResource(String id) throws ResourceNotFoundException {

		if (StringUtils.isEmpty(id))
			throw new NullPointerException("Id of the resource to be found can't be null.");

		for (IRootResource resource : resources) {
			if (StringUtils.equals(id, resource.getId()))
				return resource;
		}

		throw new ResourceNotFoundException("No resource found with id: " + id);
	}

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing RootResourceManagement");

		log.info("Initialized RootResourceManagement");
	}

	@Override
	public void deactivate() {
		log.info("Removing RootResourceManagement");

		log.info("Removed RootResourceManagement");
	}

}
