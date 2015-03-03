package org.mqnaas.test.helpers.clientprovider;

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

import java.lang.reflect.Field;

import org.mqnaas.bundletree.IClassListener;

/**
 * AbstractProviderFactory helpers to be used for testing purposes.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ProviderFactoryHelpers {

	/**
	 * Allows getting private internal {@link IClassListener} of given AbstractProviderFactory instance.
	 * 
	 * @param targetClass
	 *            class where {@link IClassListener} field is located
	 * @param providerFactory
	 *            Provider factory to be used
	 * @return internal IClassListener
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static IClassListener getInternalClassListener(Class<?> targetClass, Object providerFactory) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field internalClassListenerField = targetClass.getDeclaredField("internalClassListener");
		internalClassListenerField.setAccessible(true);
		return (IClassListener) internalClassListenerField.get(providerFactory);
	}
}
