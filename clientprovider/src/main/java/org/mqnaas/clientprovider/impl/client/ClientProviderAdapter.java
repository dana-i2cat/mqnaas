package org.mqnaas.clientprovider.impl.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.impl.ICoreModelCapability;

class ClientProviderAdapter<T, CC> implements InvocationHandler {

	private IInternalClientProvider<T, CC>	internalClientProvider;

	private ICoreModelCapability			coreModelCapability;

	public ClientProviderAdapter(IInternalClientProvider<T, CC> internalClientProvider, ICoreModelCapability coreModelCapability) {
		this.internalClientProvider = internalClientProvider;
		this.coreModelCapability = coreModelCapability;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		IResource resource = (IResource) args[0];
		IRootResource rootResource = coreModelCapability.getRootResource(resource);

		// get first endpoint
		// FIXME think a better strategy for multi-endpoint resources
		Endpoint ep = rootResource.getEndpoints().iterator().next();

		// TODO Get credentials...
		Credentials c = null;

		switch (args == null ? 0 : args.length) {
			case 1:
				return internalClientProvider.getClient(ep, c);
			case 2:
				return internalClientProvider.getClient(ep, c, (CC) args[1]);
		}

		throw new IllegalStateException("Method " + method + " is currently not mapped to the internal client provider implementation");
	}

}