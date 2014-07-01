package org.mqnaas.clientprovider.api.client;

import org.mqnaas.core.api.ICapability;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public interface IClientProviderFactory extends ICapability {

	<T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass);

}
