package org.mqnaas.clientprovider.api.client;

import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;

public interface IInternalClientProvider<T, CC> {

	T getClient(Endpoint ep, Credentials c);

	T getClient(Endpoint ep, Credentials c, CC configuration);

}
