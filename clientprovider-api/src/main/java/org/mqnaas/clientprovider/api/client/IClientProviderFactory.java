package org.mqnaas.clientprovider.api.client;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.core.api.ICapability;

public interface IClientProviderFactory extends ICapability {

	<T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass) throws ProviderNotFoundException;

	<T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass, IEndpointSelectionStrategy endpointSelectionStrategy)
			throws ProviderNotFoundException;

}
