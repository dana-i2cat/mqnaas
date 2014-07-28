package org.mqnaas.test.helpers.clientprovider;

import java.lang.reflect.Field;

import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;

/**
 * {@link AbstractProviderFactory} helpers to be used for testing purposes.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ProviderFactoryHelpers {

	/**
	 * Allows getting private internal {@link IClassListener} of given {@link AbstractProviderFactory} instance.
	 * 
	 * @param pf
	 *            AbstractProviderFactory to be used
	 * @return internal IClassListener
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static IClassListener getInternalClassListener(AbstractProviderFactory pf) throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Class<AbstractProviderFactory> pfClass = AbstractProviderFactory.class;
		Field internalClassListenerField = pfClass.getDeclaredField("internalClassListener");
		internalClassListenerField.setAccessible(true);
		return (IClassListener) internalClassListenerField.get(pf);
	}
}
