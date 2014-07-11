package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

public class RootResourceManagement implements IRootResourceManagement {

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
	public IRootResource getRootResource(Specification specification) throws ResourceNotFoundException {
		for (IRootResource resource : resources) {
			if (specification.equals(resource.getSpecification()))
				return resource;
		}

		throw new ResourceNotFoundException("No resource found with this specification: " + specification);
	}

	@Override
	public IRootResource createRootResource(RootResourceDescriptor descriptor) {
		throw new NotImplementedException();
	}

	@Override
	public IRootResource createRootResource(Specification specification, Collection<Endpoint> endpoints) {
		if (endpoints == null || endpoints.size() < 1) {
			throw new IllegalArgumentException("Invalid endpoint collection, at least one endpoint is required. Endpoints = " + endpoints);
		}

		Resource resource = new Resource(specification, endpoints);
		resources.add(resource);
		return resource;
	}

	@Override
	public void onDependenciesResolved() {
		// TODO Auto-generated method stub

	}
}
