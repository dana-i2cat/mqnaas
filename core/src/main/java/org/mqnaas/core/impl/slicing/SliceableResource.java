package org.mqnaas.core.impl.slicing;

/*
 * #%L
 * MQNaaS :: Core
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.Collection;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.core.api.slicing.SlicingException;

public class SliceableResource {

	public static boolean isSliceable(IServiceProvider sp, IResource resource) {
		boolean isSliceable = false;

		try {
			isSliceable = sp.getCapability(resource, ISlicingCapability.class) != null;
		} catch (CapabilityNotFoundException e) { 
			// ignore. capability not found.
		}

		return isSliceable;
	}

	private IServiceProvider	serviceProvider;
	private IResource	resource;
	
	public SliceableResource(IServiceProvider serviceProvider, IResource resource) {
		this.serviceProvider = serviceProvider;
		this.resource = resource;
	}
	
	private <C extends ICapability> C getCapability(Class<C> capabilityClass) {
		try {
			return serviceProvider.getCapability(resource, capabilityClass);
		} catch (CapabilityNotFoundException c) {
			throw new RuntimeException("Necessary capability not bound to resource " + resource, c);
		}
	}	

	public IResource createSlice(SliceProvidingResource slice) throws SlicingException {
		return getCapability(ISlicingCapability.class).createSlice(slice.getSlice());
	}

	public void removeSlice(IResource rootResource) throws SlicingException {
		getCapability(ISlicingCapability.class).removeSlice(rootResource);
	}

	public Collection<IResource> getSlices() {
		return getCapability(ISlicingCapability.class).getSlices();
	}
	
	public Slice getSlice() {
		return new Slice(getCapability(ISliceProvider.class).getSlice(), serviceProvider);
	}

}
