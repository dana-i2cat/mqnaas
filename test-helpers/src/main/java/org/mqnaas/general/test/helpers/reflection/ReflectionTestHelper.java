package org.mqnaas.general.test.helpers.reflection;

/*
 * #%L
 * MQNaaS :: General Test Helpers
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
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

/**
 * Reflection Test Helpers
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ReflectionTestHelper {

	/**
	 * Injects a private {@link Field} fieldInstance (instance of F) into classInstance (instance of T). It uses fieldName to look for private field.
	 * 
	 * @param classInstance
	 *            target instance
	 * @param fieldInstance
	 *            field instance to be injected
	 * @param fieldName
	 *            target field name
	 * @throws SecurityException
	 *             if this exception is thrown during the process
	 * @throws IllegalArgumentException
	 *             if fieldName could not be found in classInstance {@link Class} or if fieldInstance is not a compatible type to be injected
	 * @throws IllegalAccessException
	 *             if this exception is thrown during the process
	 */
	public static <T, F> void injectPrivateField(T classInstance, F fieldInstance, String fieldName) throws SecurityException,
			IllegalArgumentException, IllegalAccessException {

		// get all super classes and add itself
		List<Class<?>> classes = ClassUtils.getAllSuperclasses(classInstance.getClass());
		classes.add(classInstance.getClass());

		for (Class<?> clazz : classes) {

			Field field;
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// try next class
				continue;
			}
			if (!field.getType().isAssignableFrom(fieldInstance.getClass())) {
				throw new IllegalArgumentException("Invalid fieldName received, fieldInstance can not assigned to field.");
			}

			field.setAccessible(true);
			field.set(classInstance, fieldInstance);
			field.setAccessible(false);
			return;

		}

		throw new IllegalArgumentException("Invalid fieldName received, a field with this name can not be found in this class or its superclasses.");
	}

}
