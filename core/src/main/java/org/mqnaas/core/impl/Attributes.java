package org.mqnaas.core.impl;

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
