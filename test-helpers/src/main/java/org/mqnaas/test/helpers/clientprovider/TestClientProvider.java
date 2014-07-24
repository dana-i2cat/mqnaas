package org.mqnaas.test.helpers.clientprovider;

import org.mqnaas.clientprovider.api.client.IClientProvider;
import org.mqnaas.core.api.IResource;

/**
 * {@link IClientProvider} extension for testing purposes based on {@link EmptyClient} and {@link EmptyClientConfiguration}.
 * 
 * @author Julio Carlos Barrera
 *
 */
public interface TestClientProvider extends IClientProvider<EmptyClient, EmptyClientConfiguration> {

	@Override
	public EmptyClient getClient(IResource resource);

	@Override
	public EmptyClient getClient(IResource resource, EmptyClientConfiguration clientConfiguration);

}