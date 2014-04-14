package org.opennaas.core.clientprovider;



public interface IClientProvider<T extends IClient> {
	
	T getClient();
	
	T getClient(Object config);

}
