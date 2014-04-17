package test.capability;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.opennaas.core.clientprovider.IAPIProvider;
import org.opennaas.core.clientprovider.IInternalAPIProvider;
import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;

class APIProviderAdapter<CC> implements IAPIProvider<CC>, InvocationHandler {

	private IInternalAPIProvider<CC> internalAPIProvider;

	public APIProviderAdapter(IInternalAPIProvider<CC> internalAPIProvider) {
		this.internalAPIProvider = internalAPIProvider;
	}

	@Override
	public <T> T getClient(Class<T> apiClass) {
		// TODO Get endpoints and credentials...
		Endpoint ep = null;
		Credentials c = null;

		return internalAPIProvider.getClient(apiClass, ep, c);
	}

	@Override
	public <T> T getClient(Class<T> apiClass, CC clientConfiguration) {
		// TODO Get endpoints and credentials...
		Endpoint ep = null;
		Credentials c = null;

		return internalAPIProvider.getClient(apiClass, ep, c,
				clientConfiguration);
	}

	@Override
	public <T, AC> T getClient(Class<T> apiClass, CC clientConfiguration,
			AC applicationSpecificConfiguration) {
		// TODO Get endpoints and credentials...
		Endpoint ep = null;
		Credentials c = null;

		return internalAPIProvider.getClient(apiClass, ep, c,
				clientConfiguration, applicationSpecificConfiguration);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		switch (args.length) {
		case 1:
			return getClient((Class<?>) args[0]);
		case 2:
			return getClient((Class<?>) args[0], (CC) args[1]);
		case 3:
			return getClient((Class<?>) args[0], (CC) args[1], args[2]);
		}
		
		throw new IllegalStateException("No mapping for method " + method);
	}

}