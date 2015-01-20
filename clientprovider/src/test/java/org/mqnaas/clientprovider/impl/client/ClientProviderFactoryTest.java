package org.mqnaas.clientprovider.impl.client;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.core.api.ICoreModelCapability;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.mqnaas.test.helpers.capability.ArtificialBundleGuard;
import org.mqnaas.test.helpers.capability.TestCapabilitiesFactory;
import org.mqnaas.test.helpers.clientprovider.EmptyClient;
import org.mqnaas.test.helpers.clientprovider.EmptyClientConfiguration;
import org.mqnaas.test.helpers.clientprovider.ProviderFactoryHelpers;
import org.mqnaas.test.helpers.clientprovider.TestClientProvider;
import org.mqnaas.test.helpers.clientprovider.TestClientProviderFactory;
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
	public void testClientProviderFactory() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
			ProviderNotFoundException, ApplicationActivationException {

		// generate artificial objects
		IRootResource resource = TestResourceFactory.createIRootResource(null, null, null, TestResourceFactory.createFakeEndpoints(), null);
		ArtificialBundleGuard bg = TestCapabilitiesFactory.createArtificialBundleGuard();
		ICoreModelCapability cmc = TestCapabilitiesFactory.createArtificialCoreModelCapability(resource);
		EmptyClientConfiguration ecc = TestClientProviderFactory.createEmptyClientConfiguration();

		// create ClientProviderFactory, inject dummy capabilities and start it
		ClientProviderFactory cpf = new ClientProviderFactory();
		ReflectionTestHelper.<ClientProviderFactory, IBundleGuard> injectPrivateField(cpf, bg, "bundleGuard");
		ReflectionTestHelper.<ClientProviderFactory, ICoreModelCapability> injectPrivateField(cpf, cmc, "coreModelCapability");
		cpf.activate();

		// add InternalClientProvider to the system, using an artificially generated event
		bg.throwClassEntered(ProviderFactoryHelpers.getInternalClassListener(AbstractProviderFactory.class, cpf), TestInternalClientProvider.class);

		// obtain a client from client provider
		EmptyClient client = cpf.<EmptyClient, EmptyClientConfiguration, TestClientProvider> getClientProvider(TestClientProvider.class).getClient(
				resource, ecc);
		Assert.assertTrue("Client must be an instance of EmptyClient.", client instanceof EmptyClient);
	}
}
