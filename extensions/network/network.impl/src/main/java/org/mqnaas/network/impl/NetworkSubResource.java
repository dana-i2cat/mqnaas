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
		try {
			return getCapability(ISlicingCapability.class);
		} catch (RuntimeException r) {
			// i don't want to launch an exception here, since it's allowed that resources does not contain ISlicingCapability
			return null;
		}
	}

	public IRequestBasedNetworkManagement getRequestBasedNetworkManagementCapability() {
		try {
			return getCapability(IRequestBasedNetworkManagement.class);
		} catch (RuntimeException r) {
			// i don't want to launch an exception here, since it's allowed that resources does not contain IRequestBasedNetworkManagement capabiltiy
			return null;
		}
	}

	public ISliceProvider getSliceProviderCapability() {
		return getCapability(ISliceProvider.class);
	}

	private <C extends ICapability> C getCapability(Class<C> capabilityClass) {
		try {
			return serviceProvider.getCapability(resource, capabilityClass);
		} catch (CapabilityNotFoundException c) {
			throw new RuntimeException("Necessary capability not bound to resource " + resource, c);

		}
	}

}
