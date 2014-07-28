package org.mqnaas.test.helpers.clientprovider;

import java.lang.reflect.Field;

import org.mqnaas.bundletree.IClassListener;

/**
 * AbstractProviderFactory helpers to be used for testing purposes.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ProviderFactoryHelpers {

	/**
	 * Allows getting private internal {@link IClassListener} of given AbstractProviderFactory instance.
	 * 
	 * @param targetClass
	 *            class where {@link IClassListener} field is located
	 * @param providerFactory
	 *            Provider factory to be used
	 * @return internal IClassListener
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static IClassListener getInternalClassListener(Class<?> targetClass, Object providerFactory) throws SecurityException,
			NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field internalClassListenerField = targetClass.getDeclaredField("internalClassListener");
		internalClassListenerField.setAccessible(true);
		return (IClassListener) internalClassListenerField.get(providerFactory);
	}
}
