package org.mqnaas.network.impl.request;

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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.network.api.request.IRequestResourceMapping;

/**
 * Implementation of the {@link IRequestResourceMapping} capability using a {@link ConcurrentHashMap}. This implementation is bound to the
 * {@link RequestResource} to provide the mapping information necessary when creating the network.
 * 
 * This mapping is thought to be provided by different sources, e.g. the user itself, or an algorithm automatically mapping a request to an
 * infrastructure.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestResourceMapping implements IRequestResourceMapping {

	@Resource
	IResource							resource;

	private Map<IResource, IResource>	mapping;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	@Override
	public void defineMapping(IResource requestResource, IResource rootResource) {
		mapping.put(requestResource, rootResource);
	}

	@Override
	public IResource getMapping(IResource requestResource) {
		return mapping.get(requestResource);
	}

	@Override
	public void removeMapping(IResource resource) {
		mapping.remove(resource);
	}

	@Override
	public Collection<IResource> getMappedDevices() {
		return mapping.keySet();
	}

	@Override
	public void activate() throws ApplicationActivationException {
		mapping = new ConcurrentHashMap<IResource, IResource>();
	}

	@Override
	public void deactivate() {
	}

}
