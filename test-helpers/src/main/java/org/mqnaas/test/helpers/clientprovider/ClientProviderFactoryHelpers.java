package org.mqnaas.test.helpers.clientprovider;

import java.lang.reflect.Field;

import org.mqnaas.bundletree.IClassListener;
import org.mqnaas.clientprovider.impl.client.ClientProviderFactory;

/**
 * {@link ClientProviderFactory} helpers to be used for testing purposes.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ClientProviderFactoryHelpers {

	/**
	 * Allows getting private internal {@link IClassListener} of given {@link ClientProviderFactory} instance.
	 * 
	 * @param cpf
	 *            ClientProviderFactory to be used
	 * @return internal IClassListener
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static IClassListener getInternalClassListener(ClientProviderFactory cpf) throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Class<ClientProviderFactory> cpfClass = ClientProviderFactory.class;
		Field internalClassListenerField = cpfClass.getDeclaredField("internalClassListener");
		internalClassListenerField.setAccessible(true);
		return (IClassListener) internalClassListenerField.get(cpf);
	}
}
