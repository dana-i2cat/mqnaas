package org.mqnaas.bundletree;

/*
 * #%L
 * MQNaaS :: BundleTree
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

import org.mqnaas.core.api.ICapability;

/**
 * Bundle Guard interface allowing to register/unregister {@link IClassListener} with a {@link IClassFilter}. It will notify {@link Class}es
 * entering/leaving system classpath through IClassListener callback methods based on IClassFilter logic.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public interface IBundleGuard extends ICapability {

	/**
	 * Registers a {@link IClassListener} using a {@link IClassFilter}.
	 * 
	 * @param classFilter
	 *            IClassFilter to be used as class filtering logic
	 * @param classListener
	 *            IClassListener to be called back when necessary
	 */
	public void registerClassListener(IClassFilter classFilter, IClassListener classListener);

	/**
	 * Unregister a previously registered {@link IClassListener}. This operation removes first IClassListener found registered with given
	 * IClassFilter. For this reason it is recommended registering each IClassListener as a different instance.
	 * 
	 * @param classListener
	 *            IClassListener to be unregistered
	 */
	public void unregisterClassListener(IClassListener classListener);
}
