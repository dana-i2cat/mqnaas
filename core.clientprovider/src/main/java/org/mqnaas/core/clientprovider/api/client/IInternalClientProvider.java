package org.mqnaas.core.clientprovider.api.client;

import org.mqnaas.core.other.Credentials;
import org.mqnaas.core.other.Endpoint;

public interface IInternalClientProvider<T, CC> {

	T getClient(Endpoint ep, Credentials c);

	T getClient(Endpoint ep, Credentials c, CC configuration);

}
