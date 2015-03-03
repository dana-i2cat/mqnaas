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

import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;

/**
 * Implementation of {@link IBundleGuard} capability allowing to send artificially generated events using
 * {@link #throwClassEntered(IClassListener, Class)} and {@link #throwClassLeft(IClassListener, Class)} methods.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ArtificialBundleGuard implements IBundleGuard {

	// package private constructor
	ArtificialBundleGuard() {
	}

	@Override
	public void activate() {
		// nothing to do
	}

	@Override
	public void deactivate() {
		// nothing to do
	}

	@Override
	public void registerClassListener(IClassFilter classFilter, IClassListener classListener) {
		// nothing to do
	}

	@Override
	public void unregisterClassListener(IClassListener classListener) {
		// nothing to do
	}

	/**
	 * Generate an artificial {@link IClassListener#classEntered(Class)} event.
	 * 
	 * @param classListener
	 *            {@link ClassListener} to send the event
	 * @param clazz
	 *            event {@link Class}
	 */
	public void throwClassEntered(IClassListener classListener, Class<?> clazz) {
		classListener.classEntered(clazz);
	}

	/**
	 * Generate an artificial {@link IClassListener#classLeft(Class)} event.
	 * 
	 * @param classListener
	 *            {@link ClassListener} to send the event
	 * @param clazz
	 *            event {@link Class}
	 */
	public void throwClassLeft(IClassListener classListener, Class<?> clazz) {
		classListener.classLeft(clazz);
	}

}
