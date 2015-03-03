package org.mqnaas.network.impl;

/*
 * #%L
 * MQNaaS :: Network Implementation
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

/**
 * Implementation of the {@link IRootResourceProvider} backed by a {@link CopyOnWriteArrayList} binding to <b>virtual</b> networks only.
 * 
 * @author Georg Mansky-Kummert
 */
public class NetworkRootResourceProvider implements IRootResourceProvider {

	private List<IRootResource>	resources;

	@Resource
	IResource					resource;

	public static boolean isSupporting(IRootResource resource) {
		Specification specification = resource.getDescriptor().getSpecification();

		return specification.getType() == Type.NETWORK && StringUtils.equals(specification.getModel(), "virtual");
	}

	@Override
	public void activate() throws ApplicationActivationException {
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
			matches &= (type != null && specification.getType() != null) ? specification.getType().equals(type) : true;
			matches &= (model != null && specification.getModel() != null) ? specification.getModel().equals(model) : true;
			matches &= (version != null && specification.getVersion() != null) ? specification.getVersion().equals(version) : true;

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
