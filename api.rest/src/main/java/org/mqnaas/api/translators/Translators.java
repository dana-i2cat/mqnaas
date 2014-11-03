package org.mqnaas.api.translators;

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
