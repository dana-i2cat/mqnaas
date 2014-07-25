package org.mqnaas.api.translators;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.mqnaas.api.translators.ResourceTranslator.ResourceResolver;
import org.mqnaas.core.api.IIdentifiable;
import org.mqnaas.core.api.IResource;

/**
 * Translators manages available {@link Translator} implementation when mapping parameters and results between REST API and Java API interfaces.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public class Translators {

	private Map<Pair<Class<?>, Class<?>>, Translator>	translators	= new HashMap<Pair<Class<?>, Class<?>>, Translator>();

	private ResourceTranslator							resourceTranslator;

	public Translators() {
		// Configure default translators...
		// A generic translator that can be used to translate IIdentifiables to Strings
		translators.put(new ImmutablePair<Class<?>, Class<?>>(IIdentifiable.class, String.class), new Identifiable2IdTranslator());
		translators.put(new ImmutablePair<Class<?>, Class<?>>(String.class, IResource.class), resourceTranslator = new ResourceTranslator());
	}

	public Translator getTranslator(Class<?> inputClass, Class<?> outputClass) {

		Translator translator = null;

		if (!inputClass.isAssignableFrom(outputClass)) {
			for (Pair<Class<?>, Class<?>> transition : translators.keySet()) {

				Class<?> left = transition.getLeft();
				Class<?> right = transition.getRight();

				if (left.isAssignableFrom(inputClass) && outputClass.isAssignableFrom(right)) {
					translator = translators.get(transition);
					break;
				}
			}
		}

		return translator;
	}

	public Class<?> getTranslation(Class<?> clazz) {

		for (Pair<Class<?>, Class<?>> translation : translators.keySet()) {

			Class<?> left = translation.getLeft();
			Class<?> right = translation.getRight();

			if (left.isAssignableFrom(clazz)) {
				return right;
			}
		}

		return clazz;
	}

	public void addResourceResolver(ResourceResolver resourceResolver) {
		resourceTranslator.addResourceResolver(resourceResolver);
	}

}
