package org.opennaas.core.clientprovider;

public interface IASClientProvider<T> {

	T getClient(T apiClass);

	T getClient(T apiClass, Object config);

}
