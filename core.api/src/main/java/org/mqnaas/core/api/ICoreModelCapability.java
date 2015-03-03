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


/**
 * This {@link ICapability} groups all the operations that can be requested to core implementation.
 * 
 * @author Julio Carlos Barrera
 *
 */
public interface ICoreModelCapability extends ICapability {

	/**
	 * Retrieves the {@link IRootResource} that corresponds to a given {@link IResource} in core model.
	 * 
	 * @param resource
	 *            IResource from where it is necessary to look for
	 * @return IRootResource corresponding to given resource
	 * @throws IllegalArgumentException
	 *             if given resource is not present in the model
	 */
	public IRootResource getRootResource(IResource resource) throws IllegalArgumentException;

}
