package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagement;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;

public class ResourceManagement implements IResourceManagement {

	private List<IResource>	resources	= new ArrayList<IResource>();

	public static boolean isSupporting(IResource resource) {
		return resource instanceof MQNaaS;
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
