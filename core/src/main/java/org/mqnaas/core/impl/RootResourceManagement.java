package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

public class RootResourceManagement implements IRootResourceProvider, IRootResourceAdministration {

	private List<IRootResource>	resources	= new ArrayList<IRootResource>();

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType() == Specification.Type.CORE;
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
	public List<IRootResource> getRootResources(Specification.Type type, String model, String version) {
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

		return filteredResources;
	}

	public IRootResource getRootResource(Specification specification) throws ResourceNotFoundException {
		List<IRootResource> filteredResources = getRootResources(specification);

		if (filteredResources.isEmpty())
			throw new ResourceNotFoundException("No resource found with this specification: " + specification);

		return filteredResources.get(0);
	}

	public List<IRootResource> getRootResources(Specification specification) throws ResourceNotFoundException {
		List<IRootResource> filteredResources = new ArrayList<IRootResource>();
		for (IRootResource resource : resources) {
			if (specification.equals(resource.getDescriptor().getSpecification()))
				filteredResources.add(resource);
		}

		return filteredResources;
	}

	@Override
	public IRootResource createRootResource(RootResourceDescriptor descriptor) throws InstantiationException, IllegalAccessException {
		if (descriptor.getEndpoints() == null || descriptor.getEndpoints().isEmpty()) {
			throw new IllegalArgumentException(
					"Invalid endpoint collection, at least one endpoint is required. Endpoints = " + descriptor.getEndpoints());
		}

		RootResource resource = new RootResource(descriptor);
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
	public IRootResource getCore() {
		return getRootResources(Specification.Type.CORE, null, null).get(0);
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
