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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.impl.request.RequestResource;

/**
 * Implementation of the {@link ILinkManagement} capability backed by a {@link CopyOnWriteArrayList}, which is bound to all {@link IRootResource}s as
 * well as to {@link RequestResource}.
 * 
 * @author Georg Mansky-Kummert
 *
 */
public class LinkManagement implements ILinkManagement {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	public static boolean isSupporting(IRootResource resource) {
		return (resource.getDescriptor().getSpecification().getType().equals(Type.NETWORK));
	}

	private List<LinkResource>	links;

	@Override
	public IResource createLink() {
		LinkResource link = new LinkResource();
		links.add(link);
		return link;
	}

	@Override
	public void removeLink(IResource link) {
		links.remove(link);
	}

	@Override
	public List<IResource> getLinks() {
		return new ArrayList<IResource>(links);
	}

	@Override
	public void activate() throws ApplicationActivationException {
		links = new CopyOnWriteArrayList<LinkResource>();
	}

	@Override
	public void deactivate() {
	}

}
