package org.opennaas.core.clientprovider.api;

public interface IAPIProviderFactory {

	<CC, C extends IAPIProvider<CC>> C getAPIProvider(Class<C> apiProviderClass);

}
