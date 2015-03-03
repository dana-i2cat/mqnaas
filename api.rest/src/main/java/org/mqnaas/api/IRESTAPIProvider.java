package org.mqnaas.api;

/*
 * #%L
 * MQNaaS :: REST API Provider
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

import org.mqnaas.api.exceptions.InvalidCapabilityDefinionException;
import org.mqnaas.core.api.ICapability;

/**
 * Core capability offering publication of services defined in a capability in the REST API.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
public interface IRESTAPIProvider extends ICapability {

	/**
	 * Publishes the given {@link ICapability} at the given <code>URI</code>. Since a capability implementation may provide the implementation for
	 * more than one capability interface, the <code>interfaceToPublish</code> defines, which of the implemented interfaces is published.
	 * 
	 * @param capability
	 *            The implementation used to back up the published interface.
	 * @param interfaceToPublish
	 *            The specific interface published
	 * @param URI
	 *            The URI used when publishing the interface
	 * @throws InvalidCapabilityDefinionException
	 *             Thrown when the given interface violates the publication restrictions.
	 */
	void publish(ICapability capability, Class<? extends ICapability> interfaceToPublish, String URI) throws InvalidCapabilityDefinionException;

	/**
	 * Unpublishes the already published <code>interfaceToUnPublish</code>.
	 * 
	 * @param capability
	 *            The implementation used to back up the published interface.
	 * @param interfaceToUnPublish
	 *            The specific interface to unpublish.
	 * @return <code>true</code>, if the unpublication was successful.
	 */
	boolean unpublish(ICapability capability, Class<? extends ICapability> interfaceToUnPublish);
}
