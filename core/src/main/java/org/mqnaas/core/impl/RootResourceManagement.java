package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.List;

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
	public List<IRootResource> getRootResources(Specification.Type type, String model, String version) {
		List<IRootResource> filteredResources = new ArrayList<IRootResource>();

		for (IRootResource resource : getRootResources()) {

			Specification specification = resource.getSpecification();

			boolean matches = true;
			matches &= type != null ? specification.getType().equals(type) : true;
			matches &= model != null ? specification.getModel().equals(model) : true;
			matches &= version != null ? specification.getVersion().equals(version) : true;

			if (matches)
				filteredResources.add(resource);
		}

		return filteredResources;
	}

	@Override
	public IRootResource createRootResource(RootResourceDescriptor descriptor) {
		Resource resource = new Resource(descriptor.getSpecification());
		resources.add(resource);
		return resource;
	}

	@Override
	public IRootResource getCore() {
		return getRootResources(Specification.Type.CORE, null, null).get(0);
	}

}
