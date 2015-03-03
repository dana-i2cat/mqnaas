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

import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.impl.topology.port.PortResource;

/**
 * <p>
 * Wrapper class for the {@link PortResource} that provides an easier access to its capabilities and services.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class PortResourceWrapper {

	private IResource			port;
	private IServiceProvider	serviceProvider;

	public PortResourceWrapper(IResource port, IServiceProvider serviceProvider) {
		if (port == null || serviceProvider == null)
			throw new NullPointerException("PortResourceWrapper doesn't accept null values");
		
		this.port = port;
		this.serviceProvider = serviceProvider;
	}

	public String getAttribute(String name) {
		try {
			return getAttributeStore().getAttribute(name);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + port.getId(), e);
		}
	}

	public void setAttribute(String name, String value) {
		try {
			getAttributeStore().setAttribute(name, value);
		} catch (CapabilityNotFoundException c) {
			throw new RuntimeException("Necessary capability not bound to resource " + port.getId(), c);
		}
	}

	public IResource getPortResource() {
		return port;
	}

	private IAttributeStore getAttributeStore() throws CapabilityNotFoundException {
		return serviceProvider.getCapability(port, IAttributeStore.class);
	}

}
