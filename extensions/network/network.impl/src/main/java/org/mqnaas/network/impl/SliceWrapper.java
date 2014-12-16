package org.mqnaas.network.impl;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceAdministration;

class SliceWrapper {

	private IResource			slice;
	private IServiceProvider	serviceProvider;

	public SliceWrapper(IResource slice, IServiceProvider serviceProvider) {
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
