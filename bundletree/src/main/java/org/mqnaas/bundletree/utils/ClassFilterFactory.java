package org.mqnaas.bundletree.utils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

import org.mqnaas.bundletree.IClassFilter;

/**
 * {@link IClassFilter} factory allowing to create predefined instances using factory methods.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ClassFilterFactory {

	/**
	 * Retrieves an {@link IClassFilter} accepting any of given target classes in his {@link IClassFilter#filter(Class)} method.
	 * 
	 * @param targetClasses
	 * @return
	 */
	public static IClassFilter getBasicClassFilter(Collection<Class<?>> targetClasses) {
		return new BasicProviderClassFilter(targetClasses);
	}

	public static IClassFilter getBasicClassFilter(Class<?> targetClass) {
		return getBasicClassFilter(Arrays.<Class<?>> asList(targetClass));
	}

	private static class BasicProviderClassFilter implements IClassFilter {

		private Collection<Class<?>>	targetClasses;

		public BasicProviderClassFilter(Collection<Class<?>> targetClasses) {
			this.targetClasses = targetClasses;
		}

		@Override
		public boolean filter(Class<?> clazz) {
			for (Class<?> targetClass : targetClasses) {
				// retrieve only instantiable classes
				if (targetClass.isAssignableFrom(clazz) && !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
					return true;
				}
			}
			return false;
		}

	}
}
