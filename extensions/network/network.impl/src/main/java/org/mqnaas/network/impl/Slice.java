package org.mqnaas.network.impl;

/**
 * <p>
 * Wrapper class for {@link SliceResource}s to provide easier access to its capabilities.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 */
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceAdministration;

class Slice {

	private IResource			slice;
	private IServiceProvider	serviceProvider;

	public Slice(IResource slice, IServiceProvider serviceProvider) {
		this.slice = slice;
		this.serviceProvider = serviceProvider;
	}

	public ISliceAdministration getSliceAdministration() throws CapabilityNotFoundException {
		return serviceProvider.getCapability(slice, ISliceAdministration.class);
	}

	public IResource getSlice() {
		return slice;
	}
}
