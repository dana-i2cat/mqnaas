package org.mqnaas.test.helpers.capability;

/*
 * #%L
 * MQNaaS :: MQNaaS Test Helpers
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

import org.mqnaas.core.api.ICoreModelCapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;

/**
 * Artificial {@link ICoreModelCapability} that always returns given {@link IRootResource} in {@link #getRootResource(IResource)} method.
 * 
 * @author Julio Carlos Barrera
 *
 */
class ArtificialCoreModelCapability implements ICoreModelCapability {

	private IRootResource	resourceToBeReturned;

	/**
	 * Constructor to be used in order to return given {@link IRootResource} when {@link #getRootResource(IResource)} method is invoked.
	 * 
	 * @param resourceToBeReturned
	 *            IRootResource to be returned
	 */
	public ArtificialCoreModelCapability(IRootResource resourceToBeReturned) {
		this.resourceToBeReturned = resourceToBeReturned;
	}

	@Override
	public void activate() {
		// do nothing
	}

	@Override
	public void deactivate() {
		// do nothing
	}

	@Override
	public IRootResource getRootResource(IResource resource) throws IllegalArgumentException {
		return resourceToBeReturned;
	}

}
