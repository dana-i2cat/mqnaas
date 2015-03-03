package org.mqnaas.api.translators;

/*
 * #%L
 * MQNaaS :: REST API Provider
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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.mqnaas.api.translators.ResourceTranslator.ResourceResolver;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IIdentifiable;

/**
 * Translators manages available {@link Translator} implementation when mapping parameters and results between REST API and Java API interfaces.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public class Translators {

	private Map<Pair<Class<?>, Class<?>>, Translator>	resultTranslators		= new HashMap<Pair<Class<?>, Class<?>>, Translator>();
	private Map<Pair<Class<?>, Class<?>>, Translator>	parameterTranslators	= new HashMap<Pair<Class<?>, Class<?>>, Translator>();

	private ResourceTranslator							resourceTranslator;

	public Translators() {
		// Define default translators...
		resultTranslators.put(new ImmutablePair<Class<?>, Class<?>>(IIdentifiable.class, String.class), new Identifiable2IdTranslator());

		parameterTranslators.put(new ImmutablePair<Class<?>, Class<?>>(String.class, IResource.class), resourceTranslator = new ResourceTranslator());
		parameterTranslators.put(new ImmutablePair<Class<?>, Class<?>>(String.class, Class.class), new ClassTranslator());
	}

	public Translator getResultTranslator(Class<?> inputClass, Class<?> outputClass) {

		Translator translator = null;

		if (!inputClass.isAssignableFrom(outputClass)) {
			for (Pair<Class<?>, Class<?>> transition : resultTranslators.keySet()) {

				Class<?> left = transition.getLeft();
				Class<?> right = transition.getRight();

				if (left.isAssignableFrom(inputClass) && outputClass.isAssignableFrom(right)) {
					translator = resultTranslators.get(transition);
					break;
				}
			}
		}

		return translator;
	}

	public Translator getParameterTranslator(Class<?> inputClass, Class<?> outputClass) {

		Translator translator = null;

		if (!inputClass.isAssignableFrom(outputClass)) {
			for (Pair<Class<?>, Class<?>> transition : parameterTranslators.keySet()) {

				Class<?> left = transition.getLeft();
				Class<?> right = transition.getRight();

				if (right.isAssignableFrom(outputClass) && inputClass.isAssignableFrom(left)) {
					translator = parameterTranslators.get(transition);
					break;
				}
			}
		}

		return translator;
	}

	public Class<?> getResultTranslation(Class<?> clazz) {

		for (Pair<Class<?>, Class<?>> translation : resultTranslators.keySet()) {

			Class<?> left = translation.getLeft();
			Class<?> right = translation.getRight();

			if (left.isAssignableFrom(clazz)) {
				return right;
			}
		}

		return clazz;
	}

	public Class<?> getParameterTranslation(Class<?> clazz) {

		boolean isArray = clazz.isArray();
		if (isArray) {
			clazz = clazz.getComponentType();
		}

		for (Pair<Class<?>, Class<?>> translation : parameterTranslators.keySet()) {

			Class<?> left = translation.getLeft();
			Class<?> right = translation.getRight();

			if (right.isAssignableFrom(clazz)) {
				clazz = left;
				break;
			}
		}

		return isArray ? Array.newInstance(clazz, 0).getClass() : clazz;
	}

	public void addResourceResolver(ResourceResolver resourceResolver) {
		resourceTranslator.addResourceResolver(resourceResolver);
	}

}
