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
 * <p>
 * <code>IBindingDecider</code> provides the logic to decide when {@link ICapability}'s should be bound to {@link IResource}'s.
 * </p>
 */
public interface IBindingDecider extends ICapability {

	/**
	 * <p>
	 * Defines whether the services present in the given {@link ICapability} class should be bound to the given {@link IResource}. This is the service
	 * defining the automatic binding process of the platform.
	 * </p>
	 * 
	 * @param resource
	 *            The resource for which the binding is checked
	 * @param capabilityClass
	 *            The class containing an implementation of one or more {@link ICapability}s.
	 * @return whether the {@link IResource} and the {@link Class} should be bound
	 */
	boolean shouldBeBound(IResource resource, Class<? extends ICapability> capabilityClass);

}
