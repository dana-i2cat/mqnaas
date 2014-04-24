package org.opennaas.core.clientprovider.api.apiclient;

import org.opennaas.core.api.ICapability;

public interface IAPIProviderFactory extends ICapability {

	<CC, C extends IAPIClientProvider<CC>> C getAPIProvider(Class<C> apiProviderClass);

}
