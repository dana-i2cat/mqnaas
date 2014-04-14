package org.opennaas.core.clientprovider;



public interface IClientProviderFactory {

	/**
	 * Returns a provider for clients of class CLIENT
	 */
	<CLIENT extends IClient, C extends IClientProvider<CLIENT>> 
		C getClientProvider(Class<CLIENT> clientClazz);

	/**
	 * Return an application specific provider for clients of class CLIENT
	 */
	<CLIENT, C extends IASClientProvider> 
		C getASClientProvider(Class<CLIENT> clientClass);
	
	
	

}
