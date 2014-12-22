package org.mqnaas.core.impl.slicing;

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
