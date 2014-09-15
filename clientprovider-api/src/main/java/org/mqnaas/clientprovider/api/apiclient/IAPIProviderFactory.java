package org.mqnaas.clientprovider.api.apiclient;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.core.api.ICapability;

public interface IAPIProviderFactory extends ICapability {

	<CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass) throws ProviderNotFoundException;

	<CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass, IEndpointSelectionStrategy endpointSelectionStrategy)
			throws ProviderNotFoundException;

}
