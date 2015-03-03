package org.mqnaas.core.impl.utils;

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection utilities
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class ReflectionUtils {

	/**
	 * Retrieves all {@link Field} of given {@link Class} and his superclasses annotated with given {@ink Annotation} class.
	 * 
	 * @param clazz
	 *            Class to look for fields
	 * @param annotationClass
	 *            annotation class to be found
	 * @return {@link List} of annotated fields
	 */
	public static List<Field> getAnnotationFields(Class<?> clazz, Class<?> annotationClass) {
		// check attributes
		if (clazz == null || annotationClass == null) {
			throw new NullPointerException("clazz and annotationClass parameters must be not null.");
		} else if (clazz.isInterface()) {
			throw new IllegalArgumentException("clazz parameter can not be an interface, it must be a class.");
		} else if (!annotationClass.isAnnotation()) {
			throw new IllegalArgumentException("annotationClass parameter must be an annotation.");
		}

		List<Field> fields = new ArrayList<Field>();
		return getAnnotationFields(clazz, annotationClass, fields);
	}

	private static List<Field> getAnnotationFields(Class<?> clazz, Class<?> annotationClass, List<Field> currentFields) {
		for (Field field : clazz.getDeclaredFields()) {
			for (Annotation anot : field.getAnnotations()) {
				if (anot.annotationType().equals(annotationClass)) {
					currentFields.add(field);
				}
			}
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null && !superClass.equals(Object.class)) {
			getAnnotationFields(superClass, annotationClass, currentFields);
		}

		return currentFields;
	}
}
