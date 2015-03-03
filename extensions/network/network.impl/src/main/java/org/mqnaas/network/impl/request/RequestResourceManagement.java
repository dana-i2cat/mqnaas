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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.network.api.request.IRequestResourceManagement;

/**
 * Implementation of the {@link IRequestResourceManagement} capability using a {@link CopyOnWriteArrayList}. This implementation is bound to the
 * {@link RequestResource} itself and to all {@link RequestRootResource}s of type network.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestResourceManagement implements IRequestResourceManagement {

	@Resource
	IResource							resource;

	private List<RequestRootResource>	resources;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource || (resource instanceof RequestRootResource && ((RequestRootResource) resource).getType() == Type.NETWORK);
	}

	@Override
	public IResource createResource(Type type) {
		RequestRootResource resource = new RequestRootResource(type);
		resources.add(resource);
		return resource;
	}

	@Override
	public void removeResource(IResource resource) {
		resources.remove(resource);
	}

	@Override
	public List<IResource> getResources() {
		return new ArrayList<IResource>(resources);
	}

	@Override
	public void activate() throws ApplicationActivationException {
		resources = new CopyOnWriteArrayList<RequestRootResource>();
	}

	@Override
	public void deactivate() {
	}

}
