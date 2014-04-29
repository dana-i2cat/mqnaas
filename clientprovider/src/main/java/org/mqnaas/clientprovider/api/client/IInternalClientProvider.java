package org.mqnaas.clientprovider.api.client;

import org.mqnaas.core.client.other.Credentials;
import org.mqnaas.core.client.other.Endpoint;

public interface IInternalClientProvider<T, CC> {

	T getClient(Endpoint ep, Credentials c);

	T getClient(Endpoint ep, Credentials c, CC configuration);

}
