package org.mqnaas.clientprovider.api.apiclient;

import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.core.api.ICapability;

public interface IAPIProviderFactory extends ICapability {

	<CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass);

	<CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass, IEndpointSelectionStrategy endpointSelectionStrategy);

}
