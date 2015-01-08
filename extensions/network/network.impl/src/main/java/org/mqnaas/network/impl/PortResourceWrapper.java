package org.mqnaas.network.impl;

import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.network.impl.topology.port.PortResource;

/**
 * <p>
 * Wrapper class for the {@link PortResource} that provides an easier access to its capabilities and services.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class PortResourceWrapper {

	private IResource			port;
	private IServiceProvider	serviceProvider;

	public PortResourceWrapper(IResource port, IServiceProvider serviceProvider) {
		this.port = port;
		this.serviceProvider = serviceProvider;
	}

	public String getAttribute(String name) {
		try {
			return getAttributeStore().getAttribute(name);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + port.getId(), e);
		}
	}

	public void setAttribute(String name, String value) {
		try {
			getAttributeStore().setAttribute(name, value);
		} catch (CapabilityNotFoundException c) {
			throw new RuntimeException("Necessary capability not bound to resource " + port.getId(), c);
		}
	}

	private IAttributeStore getAttributeStore() throws CapabilityNotFoundException {
		return serviceProvider.getCapability(port, IAttributeStore.class);
	}

}
