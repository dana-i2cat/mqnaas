package org.mqnaas.network.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

/**
 * Implementation of the {@link IRootResourceProvider} backed by a {@link CopyOnWriteArrayList} binding to <b>virtual</b> networks only.
 * 
 * @author Georg Mansky-Kummert
 */
public class NetworkRootResourceProvider implements IRootResourceProvider {

	private List<IRootResource>	resources;

	public static boolean isSupporting(IRootResource resource) {
		Specification specification = resource.getDescriptor().getSpecification();

		return specification.getType() == Type.NETWORK && StringUtils.equals(specification.getModel(), "virtual");
	}

	@Override
	public void activate() {
		resources = new CopyOnWriteArrayList<IRootResource>();
	}

	@Override
	public void deactivate() {
	}

	@Override
	public List<IRootResource> getRootResources() {
		return new ArrayList<IRootResource>(resources);
	}

	@Override
	public void setRootResources(Collection<IRootResource> rootResources) {
		resources.clear();
		resources.addAll(rootResources);
	}

	@Override
	public List<IRootResource> getRootResources(Type type, String model, String version) throws ResourceNotFoundException {
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

	@Override
	public IRootResource getRootResource(String id) throws ResourceNotFoundException {
		for (IRootResource resource : resources) {
			if (StringUtils.equals(id, resource.getId()))
				return resource;
		}

		throw new ResourceNotFoundException("No resource found with this id: " + id);
	}

}
