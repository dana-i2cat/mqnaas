package org.mqnaas.core.clientprovider.impl.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.mqnaas.core.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.core.other.Credentials;
import org.mqnaas.core.other.Endpoint;

class ClientProviderAdapter<T, CC> implements InvocationHandler {

	private IInternalClientProvider<T, CC>	internalClientProvider;

	public ClientProviderAdapter(IInternalClientProvider<T, CC> internalClientProvider) {
		this.internalClientProvider = internalClientProvider;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		// TODO Get endpoints and credentials...
		Endpoint ep = null;
		Credentials c = null;

		switch (args == null ? 0 : args.length) {
			case 0:
				return internalClientProvider.getClient(ep, c);
			case 1:
				return internalClientProvider.getClient(ep, c, (CC) args[0]);
		}

		throw new IllegalStateException("Method " + method + " is currently not mapped to the internal client provider implementation");
	}

}