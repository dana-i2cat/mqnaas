package org.mqnaas.api.translators;

import org.mqnaas.core.api.IIdentifiable;

/**
 * A translator translating an {@link IIdentifiable} into a String using it's {@link IIdentifiable#getId()} method.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
class Identifiable2IdTranslator implements Translator {
	@Override
	public Object translate(Object input) {
		IIdentifiable identifiable = (IIdentifiable) input;
		return identifiable.getId();
	}

	@Override
	public String toString() {
		return "Default Identifieable to ID translator";
	}
}