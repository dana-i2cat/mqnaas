package org.opennaas.core.clientprovider;

public interface IAPIProvider<CC> {

	<T> T getClient(Class<T> apiClass);

	<T> T getClient(Class<T> apiClass, CC clientConfiguration);

	<T, AC> T getClient(Class<T> apiClass, CC clientConfiguration, AC applicationSpecificConfiguration);
}
