package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

public class RootResourceManagement implements IRootResourceProvider, IRootResourceAdministration {

	private List<IRootResource>	resources	= new ArrayList<IRootResource>();

	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Specification.Type.CORE;
	}

	@Override
	public void removeRootResource(IRootResource resource) {
		resources.remove(resource);
	}

	@Override
	public List<IRootResource> getRootResources() {
		return new ArrayList<IRootResource>(resources);
	}

	@Override
	public void setRootResources(List<IRootResource> resources) {
		this.resources = new ArrayList<IRootResource>(resources);

	}

	@Override
	public IRootResource getRootResource(Specification specification) throws ResourceNotFoundException {
		for (IRootResource resource : resources) {
			if (specification.equals(resource.getSpecification()))
				return resource;
		}

		throw new ResourceNotFoundException("No resource found with this specification: " + specification);
	}

	@Override
	public List<IRootResource> getRootResources(Type type, String model, String version) throws ResourceNotFoundException {

		List<IRootResource> resources = new ArrayList<IRootResource>();

		for (IRootResource resource : resources) {
			Specification spec = resource.getSpecification();
			if (spec.getModel().equals(model) && spec.getType().equals(type) && spec.getVersion().equals(version))
				resources.add(resource);
		}

		return resources;
	}

	@Override
	public IRootResource createRootResource(RootResourceDescriptor descriptor) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IRootResource createRootResource(Specification specification, Collection<Endpoint> endpoints) {
		if (endpoints == null || endpoints.size() < 1) {
			throw new IllegalArgumentException("Invalid endpoint collection, at least one endpoint is required. Endpoints = " + endpoints);
		}

		RootResource resource = new RootResource(specification, endpoints, UUID.randomUUID().toString());
		resources.add(resource);
		return resource;
	}

	@Override
	public IRootResource getRootResource(String id) throws ResourceNotFoundException {
		for (IRootResource resource : resources) {
			if (StringUtils.equals(id, resource.getId()))
				return resource;
		}

		throw new ResourceNotFoundException("No resource found with this id: " + id);
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

}
