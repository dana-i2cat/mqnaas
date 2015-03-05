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

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.api.topology.port.IPortManagement;

/**
 * <p>
 * Wrapper class that provides an easier access the {@link ICapability ICapabilities} of a {@link IResource}
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class NetworkSubResource {

	private IResource			resource;
	private IServiceProvider	serviceProvider;

	public NetworkSubResource(IResource netRootResource, IServiceProvider serviceProvider) {
		this.resource = netRootResource;
		this.serviceProvider = serviceProvider;
	}

	public ISlicingCapability getSlicingCapability() {
		try {
			return getCapability(ISlicingCapability.class);
		} catch (RuntimeException r) {
			// i don't want to launch an exception here, since it's allowed that resources does not contain ISlicingCapability
			return null;
		}
	}

	public IRequestBasedNetworkManagement getRequestBasedNetworkManagementCapability() {
		try {
			return getCapability(IRequestBasedNetworkManagement.class);
		} catch (RuntimeException r) {
			// i don't want to launch an exception here, since it's allowed that resources does not contain IRequestBasedNetworkManagement capabiltiy
			return null;
		}
	}

	public IResource getResource() {
		return resource;
	}

	public ISliceProvider getSliceProviderCapability() {
		return getCapability(ISliceProvider.class);
	}

	private <C extends ICapability> C getCapability(Class<C> capabilityClass) {
		try {
			return serviceProvider.getCapability(resource, capabilityClass);
		} catch (CapabilityNotFoundException c) {
			throw new RuntimeException("Necessary capability not bound to resource " + resource, c);

		}
	}

	public IResource getSlice() {
		return getSliceProviderCapability().getSlice();
	}

	public IResource createPort() {
		return getCapability(IPortManagement.class).createPort();
	}

	public List<IResource> getPorts() {
		return getCapability(IPortManagement.class).getPorts();
	}

	public void removePort(IResource port) {
		getCapability(IPortManagement.class).removePort(port);

	}

}
