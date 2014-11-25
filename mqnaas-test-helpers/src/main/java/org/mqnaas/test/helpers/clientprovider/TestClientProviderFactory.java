package org.mqnaas.test.helpers.clientprovider;

/**
 * Factory providing test implementations of clients, client configurations and providers.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TestClientProviderFactory {

	/**
	 * Retrieves an {@link EmptyClientConfiguration}.
	 * 
	 * @see EmptyClientConfiguration
	 * 
	 */
	public static EmptyClientConfiguration createEmptyClientConfiguration() {
		return new EmptyClientConfiguration();
	}
}
