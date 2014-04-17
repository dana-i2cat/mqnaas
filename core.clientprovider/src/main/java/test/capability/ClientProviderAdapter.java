package test.capability;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.opennaas.core.clientprovider.IClientProvider;
import org.opennaas.core.clientprovider.IInternalClientProvider;
import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;

class ClientProviderAdapter<T, CC> implements IClientProvider<T, CC>, InvocationHandler {

	private IInternalClientProvider<T, CC> internalClientProvider;

	public ClientProviderAdapter(IInternalClientProvider<T, CC> internalClientProvider) {
		this.internalClientProvider = internalClientProvider;
	}
	
	@Override
	public T getClient() {
		// TODO Get endpoints and credentials...
		Endpoint ep = null;
		Credentials c = null;
		
		return internalClientProvider.getClient(ep, c);
	}

	@Override
	public T getClient(CC clientConfiguration) {
		// TODO Get endpoints and credentials...
		Endpoint ep = null;
		Credentials c = null;

		return internalClientProvider.getClient(ep, c, clientConfiguration);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return args == null ? getClient() : getClient((CC) args[0]);
	}
	
}