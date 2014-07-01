package org.mqnaas.clientprovider.api.apiclient;

import org.mqnaas.core.api.ICapability;

/**
 * TODO Javadoc
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public interface IAPClientProviderFactory extends ICapability {

	<CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass);

}
