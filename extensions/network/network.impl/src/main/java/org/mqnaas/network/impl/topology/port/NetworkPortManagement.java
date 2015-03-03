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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;
import org.mqnaas.network.impl.request.RequestResource;

/**
 * Implementation of the {@link INetworkPortManagement} capability using a {@link CopyOnWriteArrayList}. This implementation is bound to networks.
 * 
 * @author Georg Mansky-Kummert
 */
public class NetworkPortManagement implements INetworkPortManagement {

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType().equals(Type.NETWORK);
	}

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	private List<PortResource>	ports;

	@Override
	public void activate() throws ApplicationActivationException {
		ports = new CopyOnWriteArrayList<PortResource>();
	}

	@Override
	public void deactivate() {
	}

	@Override
	public void addPort(IResource port) {
		if (!(port instanceof PortResource))
			throw new IllegalArgumentException("Given resource is not a port!");
		ports.add((PortResource) port);
	}

	@Override
	public void removePort(IResource port) {
		ports.remove(port);
	}

	@Override
	public List<IResource> getPorts() {
		return new ArrayList<IResource>(ports);
	}

}
