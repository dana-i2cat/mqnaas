package org.mqnaas.bundletree.utils;

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
	public static IClassFilter createBasicClassFilter(Collection<Class<?>> targetClasses) {
		return new BasicProviderClassFilter(targetClasses);
	}

	public static IClassFilter createBasicClassFilter(Class<?> targetClass) {
		return createBasicClassFilter(Arrays.<Class<?>> asList(targetClass));
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
