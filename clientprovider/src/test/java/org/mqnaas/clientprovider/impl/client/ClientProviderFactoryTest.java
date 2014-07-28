package org.mqnaas.clientprovider.impl.client;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.impl.ICoreModelCapability;
import org.mqnaas.test.helpers.ReflectionTestHelper;
import org.mqnaas.test.helpers.capability.ArtificialBundleGuard;
import org.mqnaas.test.helpers.capability.ArtificialCoreModelCapability;
import org.mqnaas.test.helpers.clientprovider.EmptyClient;
import org.mqnaas.test.helpers.clientprovider.EmptyClientConfiguration;
import org.mqnaas.test.helpers.clientprovider.ProviderFactoryHelpers;
import org.mqnaas.test.helpers.clientprovider.TestClientProvider;
import org.mqnaas.test.helpers.clientprovider.TestInternalClientProvider;
import org.mqnaas.test.helpers.resource.TestResourceFactory;

/**
 * Unit tests for {@link ClientProviderFactory}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ClientProviderFactoryTest {

	@Test
	public void testClientProviderFactory() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		// generate artificial objects
		IRootResource resource = TestResourceFactory.generateIRootResource(null, null, null, Arrays.asList(new Endpoint()));
		ArtificialBundleGuard bg = new ArtificialBundleGuard();

		// create ClientProviderFactory, inject dummy capabilities and start it
		ClientProviderFactory cpf = new ClientProviderFactory();
		ReflectionTestHelper.<ClientProviderFactory, IBundleGuard> injectPrivateField(cpf, bg, "bundleGuard");
		ReflectionTestHelper.<ClientProviderFactory, ICoreModelCapability> injectPrivateField(cpf, new ArtificialCoreModelCapability(resource),
				"coreModelCapability");
		cpf.activate();

		// add InternalClientProvider
		bg.throwClassEntered(ProviderFactoryHelpers.getInternalClassListener(AbstractProviderFactory.class, cpf), TestInternalClientProvider.class);

		// obtain a client from client provider
		EmptyClient client = cpf.<EmptyClient, EmptyClientConfiguration, TestClientProvider> getClientProvider(TestClientProvider.class)
				.getClient(resource);
		Assert.assertTrue("Client must be an instance of EmptyClient.", client instanceof EmptyClient);
	}
}
