package org.mqnaas.api.translators;

/**
 * <p>
 * A Translator translates input to output.
 * </p>
 * <p>
 * In the context of the API, translators are used to translate
 * <ul>
 * <li>parameters of incoming API calls to respective Java calls, and to translate</li>
 * <li>the results of the Java call.</li>
 * </ul>
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
public interface Translator {
	Object translate(Object input);
}