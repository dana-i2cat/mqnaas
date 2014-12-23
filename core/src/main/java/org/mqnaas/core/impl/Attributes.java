package org.mqnaas.core.impl;

import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;

/**
 * Wrapper to access the {@link IAttributeStore} capability of a resource.
 * 
 * @author Georg Mansky-Kummert
 */
public class Attributes {

	public static boolean is(IServiceProvider serviceProvider, IResource resource) {
		try {
			return serviceProvider.getCapability(resource, IAttributeStore.class) != null;
		} catch (CapabilityNotFoundException e) {
			return false;
		}
	}

	private IResource			resource;
	private IServiceProvider	serviceProvider;

	public Attributes(IServiceProvider serviceProvider, IResource resource) {
		this.resource = resource;
		this.serviceProvider = serviceProvider;
	}

	private <C extends ICapability> C getCapability(Class<C> capabilityClass) {
		try {
			return serviceProvider.getCapability(resource, capabilityClass);
		} catch (CapabilityNotFoundException e) {
			throw new RuntimeException("Necessary capability not bound to resource " + resource, e);
		}
	}

	public String getAttribute(String name) {
		return getCapability(IAttributeStore.class).getAttribute(name);
	}

	public void setAttribute(String name, String value) {
		getCapability(IAttributeStore.class).setAttribute(name, value);
	}
	
	

}
