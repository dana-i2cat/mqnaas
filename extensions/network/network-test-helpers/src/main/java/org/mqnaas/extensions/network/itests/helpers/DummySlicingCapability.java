package org.mqnaas.extensions.network.itests.helpers;

/*
 * #%L
 * MQNaaS :: Network :: iTests Helpers
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
import java.util.Collection;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.core.api.slicing.SlicingException;
import org.mqnaas.core.impl.RootResource;

public class DummySlicingCapability implements ISlicingCapability {

	@Resource
	IRootResource			resource;

	Collection<IResource>	slices;

	public static boolean isSupporting(IRootResource resource) {
		Type type = resource.getDescriptor().getSpecification().getType();
		return ((type != Type.NETWORK) && (type != Type.CORE));
	}

	@Override
	public void activate() throws ApplicationActivationException {
		slices = new ArrayList<IResource>();
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	@Override
	public IResource createSlice(IResource slice) throws SlicingException {

		try {
			IRootResource createdResource = new RootResource(RootResourceDescriptor.create(resource.getDescriptor().getSpecification()
					.clone(), resource.getDescriptor().getEndpoints()));
			createdResource.getDescriptor().getSpecification().setModel("virtual");

			slices.add(createdResource);

			return createdResource;

		} catch (Exception e) {
			throw new SlicingException(e);
		}

	}

	@Override
	public void removeSlice(IResource rootResource) throws SlicingException {
		slices.remove(rootResource);
	}

	@Override
	public Collection<IResource> getSlices() {
		return slices;
	}
}
