package org.mqnaas.network.impl.topology.link;

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
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.network.api.topology.link.ILinkAdministration;

/**
 * Implementation of the {@link ILinkAdministration} capability which is bound to a {@link LinkResource}.
 * 
 * @author Georg Mansky-Kummert
 */
public class LinkAdministration implements ILinkAdministration {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof LinkResource;
	}

	private IResource					srcPort, destPort;

	@DependingOn
	private IResourceManagementListener	resourceManagementListener;

	@Override
	public void activate() throws ApplicationActivationException {
		// TODO: persistence
	}

	@Override
	public void deactivate() {
		// TODO: persistence
	}

	@Override
	public IResource getSrcPort() {
		return srcPort;
	}

	@Override
	public void setSrcPort(IResource srcPort) {
		this.srcPort = srcPort;
	}

	@Override
	public IResource getDestPort() {
		return destPort;
	}

	@Override
	public void setDestPort(IResource destPort) {
		this.destPort = destPort;
	}

}
