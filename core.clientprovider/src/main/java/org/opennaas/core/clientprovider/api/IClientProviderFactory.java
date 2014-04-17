package org.opennaas.core.clientprovider.api;

public interface IClientProviderFactory {

	<T, CC, C extends IClientProvider<T, CC>> C getClientProvider(Class<C> clientProviderClass);

}
