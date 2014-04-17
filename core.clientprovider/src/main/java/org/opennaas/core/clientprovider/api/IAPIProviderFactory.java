package org.opennaas.core.clientprovider.api;

import org.opennaas.core.api.ICapability;

public interface IAPIProviderFactory extends ICapability {

	<CC, C extends IAPIProvider<CC>> C getAPIProvider(Class<C> apiProviderClass);

}
