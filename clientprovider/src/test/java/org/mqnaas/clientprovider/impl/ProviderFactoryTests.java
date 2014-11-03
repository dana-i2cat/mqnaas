package org.mqnaas.clientprovider.impl;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.client.cxf.CXFConfiguration;
import org.mqnaas.client.cxf.ICXFAPIProvider;
import org.mqnaas.client.cxf.InternalCXFClientProvider;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProvider;
import org.mqnaas.clientprovider.api.apiclient.IInternalAPIClientProvider;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.core.api.IResource;

public class ProviderFactoryTests {

	private static Set<Type>	VALID_API_PROVIDERS;

	static {
		VALID_API_PROVIDERS = new HashSet<Type>();
		VALID_API_PROVIDERS.add(IAPIClientProvider.class);
		VALID_API_PROVIDERS.add(IInternalAPIClientProvider.class);
	}

	private class TestCXFAPIProvider implements ICXFAPIProvider {

		@Override
		public <T, AC> T getAPIClient(IResource resource, Class<T> apiClass, CXFConfiguration clientConfiguration,
				AC applicationSpecificConfiguration)
				throws EndpointNotFoundException {
			return null;
		}

		@Override
		public <T> T getAPIClient(IResource resource, Class<T> apiClass, CXFConfiguration clientConfiguration) throws EndpointNotFoundException {
			return null;
		}

		@Override
		public <T> T getAPIClient(IResource resource, Class<T> apiClass) throws EndpointNotFoundException {
			return null;
		}
	};

	@Test
	public void testTypeArgumentsMatch() {
		InternalCXFClientProvider<CXFConfiguration> internalCXFClientProvider = new InternalCXFClientProvider<CXFConfiguration>();
		TestCXFAPIProvider cxfapiProvider = new TestCXFAPIProvider();

		boolean result = AbstractProviderFactory.doTypeArgumentsMatch(VALID_API_PROVIDERS, internalCXFClientProvider.getClass(),
				cxfapiProvider.getClass());

		Assert.assertTrue("Argument types must match.", result);
	}
}
