package org.opennaas.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.api.IResource;
import org.opennaas.core.api.IResourceManagement;
import org.opennaas.core.api.RootResourceDescriptor;
import org.opennaas.core.api.Specification;

public class ResourceManagement implements IResourceManagement {

	private List<IResource>	resources	= new ArrayList<IResource>();

	public static boolean isSupporting(IResource resource) {
		return resource instanceof OpenNaaS;
	}

	@Override
	public void removeRootResource(IResource resource) {
		resources.remove(resource);
	}

	@Override
	public List<IResource> getRootResources() {
		return new ArrayList<IResource>(resources);
	}

	@Override
	public <R extends IResource> R getRootResource(Class<R> clazz) {
		for (IResource resource : resources) {
			if (clazz.isInstance(resource))
				return (R) resource;
		}

		return null;
	}

	@Override
	public void createRootResource(RootResourceDescriptor descriptor) {
	}

	@Override
	public void createRootResource(Specification specification) {
	}
}
