package org.mqnaas.core.api;

/*
 * #%L
 * MQNaaS :: Core.API
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

import org.mqnaas.core.api.exceptions.ApplicationNotFoundException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;

/**
 * <code>IResourceManagementListener</code> is responsible of receiving notifications of {@link IResourceManagement} whenever a {@link IResource} is
 * added to or removed from the platform.
 * 
 * One may observe services in this ICapability to react to Resource creation and removal in the platform. See {@link IObservationService}.
 * 
 */
public interface IResourceManagementListener extends ICapability {
	/**
	 * <p>
	 * This is the service called by the {@link IResourceManagement} whenever a new {@link IResource} was added to the platform.
	 * </p>
	 * <p>
	 * 
	 * @param resource
	 *            The resource added to the platform
	 * @param managedBy
	 *            The IApplication managing given resource
	 * @param parentInterface
	 *            The interface managing given resource in managedBy instance
	 * @throws ApplicationNotFoundException
	 * @throws CapabilityNotFoundException
	 */
	void resourceAdded(IResource resource, IApplication managedBy, Class<? extends IApplication> parentInterface) throws CapabilityNotFoundException,
			ApplicationNotFoundException;

	/**
	 * <p>
	 * This is the service called by the {@link IResourceManagement} whenever a new {@link IResource} was removed from the platform.
	 * </p>
	 * <p>
	 * 
	 * @param resource
	 *            The resource removed from the platform
	 * @param managedBy
	 *            The IApplication managing given resource
	 * @param parentInterface
	 *            The interface managing given resource in managedBy instance
	 */
	void resourceRemoved(IResource resource, IApplication managedBy, Class<? extends IApplication> parentInterface);
}
