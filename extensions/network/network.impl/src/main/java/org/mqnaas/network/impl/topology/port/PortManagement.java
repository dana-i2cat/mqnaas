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
import org.mqnaas.network.api.topology.port.IPortManagement;
import org.mqnaas.network.impl.request.RequestRootResource;

/**
 * Implementation of the {@link IPortManagement} capability using a {@link CopyOnWriteArrayList}. This implementation is bound to all
 * {@link IRootResource}s except core and network ones and, in addition, to all {@link RequestRootResource}s.
 * 
 * @author Georg Mansky-Kummert
 */
public class PortManagement implements IPortManagement {

	public static boolean isSupporting(IRootResource resource) {
		Type type = resource.getDescriptor().getSpecification().getType();

		return (!type.equals(Type.CORE) && !type.equals(Type.NETWORK));
	}

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestRootResource;
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
	public IResource createPort() {
		PortResource port = new PortResource();
		ports.add(port);
		return port;
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
