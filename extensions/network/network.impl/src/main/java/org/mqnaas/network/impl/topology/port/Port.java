package org.mqnaas.network.impl.topology.port;

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

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.api.topology.port.IPortAdministration;

public class Port {

	private IResource	port;
	private IServiceProvider serviceProvider;

	public Port(IResource port, IServiceProvider serviceProvider) {
		this.port = port;
		this.serviceProvider = serviceProvider;
	}

	private IPortAdministration getPortAdministration()
			throws CapabilityNotFoundException {
		return serviceProvider.getCapability(port,
				IPortAdministration.class);
	}

	public void setName(String name) throws CapabilityNotFoundException {
		getPortAdministration().setName(name);
	}

	public IResource getResource() {
		return port;
	}

}