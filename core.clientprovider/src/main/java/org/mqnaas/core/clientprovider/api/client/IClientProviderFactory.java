package org.mqnaas.core.clientprovider.api.client;

import org.mqnaas.core.api.ICapability;

public interface IClientProviderFactory extends ICapability {

	<T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass);

}
