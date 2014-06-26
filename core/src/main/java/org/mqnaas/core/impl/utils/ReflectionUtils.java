package org.mqnaas.core.impl.utils;

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
