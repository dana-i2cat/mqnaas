package org.mqnaas.core.impl.slicing;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceProvider;

public class SliceProvidingResource {

	public static boolean isSliceproviding(IServiceProvider sp, IResource resource) {
		boolean isSliceproviding = false;

		try {
			isSliceproviding = sp.getCapability(resource, ISliceProvider.class) != null;
		} catch (CapabilityNotFoundException e) {
			// ignore. capability not found.
		}

		return isSliceproviding;
	}

	private IServiceProvider	serviceProvider;
	private IResource			resource;

	public SliceProvidingResource(IServiceProvider serviceProvider, IResource resource) {
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

	public IResource getSlice() {
		return getCapability(ISliceProvider.class).getSlice();
	}

}
