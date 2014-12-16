package org.mqnaas.network.impl;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;

/**
 * <p>
 * Wrapper class that provides an easier access the {@link ICapability ICapabilities} of a {@link IResource}
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class NetworkSubResource {

	private IResource			resource;
	private IServiceProvider	serviceProvider;

	public NetworkSubResource(IResource netRootResource, IServiceProvider serviceProvider) {
		this.resource = netRootResource;
		this.serviceProvider = serviceProvider;
	}

	public ISlicingCapability getSlicingCapability() {
		return getCapability(ISlicingCapability.class);

	}

	public IRequestBasedNetworkManagement getRequestBasedNetworkManagementCapability() {
		return getCapability(IRequestBasedNetworkManagement.class);
	}

	public ISliceProvider getSliceProviderCapability() {
		return getCapability(ISliceProvider.class);
	}

	private <C extends ICapability> C getCapability(Class<C> capabilityClass) {
		try {
			return serviceProvider.getCapability(resource, capabilityClass);
		} catch (CapabilityNotFoundException c) {
			return null;
		}
	}

}
