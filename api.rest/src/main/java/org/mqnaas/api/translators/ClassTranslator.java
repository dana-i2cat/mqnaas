package org.mqnaas.api.translators;

public class ClassTranslator implements Translator {

	@Override
	public Object translate(Object input) {

		String className = (String) input;
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Hmmm?
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
