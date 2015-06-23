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
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.mqnaas.core.impl.slicing.Slice;
import org.mqnaas.core.impl.slicing.SliceResource;
import org.mqnaas.core.impl.slicing.SliceableResource;
import org.mqnaas.core.impl.slicing.Unit;
import org.mqnaas.network.api.request.IRequestResourceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link IRequestResourceMapping} capability using a {@link ConcurrentHashMap}. This implementation is bound to the
 * {@link RequestResource} to provide the mapping information necessary when creating the network.
 * 
 * This mapping is thought to be provided by different sources, e.g. the user itself, or an algorithm automatically mapping a request to an
 * infrastructure.
 * 
 * @author Georg Mansky-Kummert
 * @author Adrián Roselló Rey (i2CAT)
 */
public class RequestResourceMapping implements IRequestResourceMapping {

	private static final Logger			log	= LoggerFactory.getLogger(RequestResourceMapping.class);

	@Resource
	IResource							resource;

	@DependingOn(core = true)
	IServiceProvider					serviceProvider;

	private Map<IResource, IResource>	mapping;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	/**
	 * Map requested {@link RequestResource} to a physical {@link IResource}. Additionally, if the second parameter is a {@link IRootResource}
	 * instance, it creates same slices units and ranges in the request resource slice.
	 * 
	 */
	@Override
	public void defineMapping(IResource requestResource, IResource rootResource) {
		mapping.put(requestResource, rootResource);

		if (rootResource instanceof IRootResource && SliceableResource.isSliceable(serviceProvider, rootResource))
			try {
				inheriteSliceInformation(requestResource, rootResource);
			} catch (CapabilityNotFoundException e) {
				log.warn("Could not automatically create slice units on virtual resource. Process should be manually done.");
			}
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

	private void inheriteSliceInformation(IResource requestResource, IResource rootResource) throws CapabilityNotFoundException {
		Slice srcSlice = new Slice(getSlice(rootResource), serviceProvider);
		Slice dstSlice = new Slice(getSlice(requestResource), serviceProvider);

		for (Unit srcUnit : srcSlice.getUnits()) {
			Unit dstUnit = dstSlice.addUnit(srcUnit.getName());
			dstUnit.setRange(srcUnit.getRange());
		}

	}

	/**
	 * Returns the {@link SliceResource} provided by a specific {@link IResource}
	 * 
	 * @param resource
	 *            Resource containing the slice to be retrieved.
	 * @return The {@link SliceResource} instance of the specified <literal>resource</literal>.
	 * @throws CapabilityNotFoundException
	 *             If the resource have not bound {@link ISliceProvider} capability.
	 */
	private IResource getSlice(IResource resource) throws CapabilityNotFoundException {
		return serviceProvider.getCapability(resource, ISliceProvider.class).getSlice();
	}

}
