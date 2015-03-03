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

import java.lang.reflect.Method;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.ApplicationNotFoundException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link ISliceProvider} capability that at the moment only binds to {@link Type#TSON}s.
 * 
 * On activation the slice resource is created and bound.
 * 
 * @author Georg Mansky-Kummert
 */
public class SliceProvider implements ISliceProvider {

	private static final Logger			log	= LoggerFactory.getLogger(SliceProvider.class);

	private IResource					slice;

	@Resource
	private IResource					resource;

	@DependingOn
	private IResourceManagementListener	resourceManagementListener;

	@Override
	public IResource getSlice() {
		return slice;
	}

	public static boolean isSupporting(IRootResource resource) {
		Type resourceType = resource.getDescriptor().getSpecification().getType();

		return resourceType.equals(Type.TSON) || resourceType.equals(Type.OF_SWITCH) || resourceType.equals(Type.CPE) || resourceType
				.equals(Type.ARN);
	}

	public static boolean isSupporting(IResource resource) {
		boolean isSupporting = false;

		// FIXME Needs to be refactoring once the binding mechanism was reworked
		if (resource.getClass().getName().equals("org.mqnaas.network.impl.request.RequestRootResource")) {

			try {
				Method m = resource.getClass().getMethod("getType");

				Type type = (Type) m.invoke(resource);

				isSupporting = type.equals(Type.TSON) || type.equals(type.OF_SWITCH) || type.equals(type.ARN) || type.equals(type.CPE);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return isSupporting;
	}

	@Override
	public void activate() throws ApplicationActivationException {

		log.info("Initializing SliceProvider capability for resource " + resource.getId());
		// TODO: persistence
		slice = new SliceResource();

		log.debug("Created " + slice.getId() + " in resource " + resource.getId());

		// Add resource manually to the platform
		try {
			resourceManagementListener.resourceAdded(slice, this, ISliceProvider.class);
		} catch (CapabilityNotFoundException e) {
			throw new ApplicationActivationException(e);
		} catch (ApplicationNotFoundException e) {
			throw new ApplicationActivationException(e);

		}

		log.info("Initialized SliceProvider capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing SliceProvider capability for resource " + resource.getId());

		// TODO: persistence

		// Remove resource manually from the platform
		resourceManagementListener.resourceRemoved(slice, this, ISliceProvider.class);

		slice = null;

		log.info("Removed SliceProvider capability for resource " + resource.getId());

	}
}
