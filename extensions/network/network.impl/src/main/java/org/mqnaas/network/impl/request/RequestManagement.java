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
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.network.api.request.IRequestManagement;

/**
 * Implementation of the {@link IRequestManagement} capability using a {@link CopyOnWriteArrayList}. This implementation is bound to
 * {@link Type#NETWORK}s.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestManagement implements IRequestManagement {

	private List<RequestResource>	requests;

	public static boolean isSupporting(IRootResource resource) {
		return (resource.getDescriptor().getSpecification().getType().equals(Type.NETWORK));
	}

	@Override
	public IResource createRequest() {
		RequestResource request = new RequestResource();
		requests.add(request);
		return request;
	}

	@Override
	public void removeRequest(IResource request) {
		requests.remove(request);
	}

	@Override
	public List<IResource> getRequests() {
		return new ArrayList<IResource>(requests);
	}

	@Override
	public void activate() throws ApplicationActivationException {
		requests = new CopyOnWriteArrayList<RequestResource>();
	}

	@Override
	public void deactivate() {
	}

}
