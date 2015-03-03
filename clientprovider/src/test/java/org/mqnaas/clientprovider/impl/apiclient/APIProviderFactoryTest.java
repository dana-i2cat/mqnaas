package org.mqnaas.clientprovider.impl.apiclient;

/*
 * #%L
 * MQNaaS :: Client Provider
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.clientprovider.api.IEndpointSelectionStrategy;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.clientprovider.impl.AbstractProviderFactory;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICoreModelCapability;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.general.test.helpers.reflection.ReflectionTestHelper;
import org.mqnaas.test.helpers.capability.ArtificialBundleGuard;
import org.mqnaas.test.helpers.capability.TestCapabilitiesFactory;
import org.mqnaas.test.helpers.clientprovider.EmptyClientAPI;
import org.mqnaas.test.helpers.clientprovider.EmptyClientConfiguration;
import org.mqnaas.test.helpers.clientprovider.ProviderFactoryHelpers;
import org.mqnaas.test.helpers.clientprovider.TestAPIProvider;
import org.mqnaas.test.helpers.clientprovider.TestClientProviderFactory;
import org.mqnaas.test.helpers.clientprovider.TestInternalAPIProvider;
import org.mqnaas.test.helpers.resource.TestResourceFactory;

/**
 * Unit tests for {@link APIProviderFactory}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class APIProviderFactoryTest {

	@Test
	public void testAPIProviderFactory() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
			EndpointNotFoundException, ProviderNotFoundException, ApplicationActivationException {

		// generate artificial objects
		IRootResource resource = TestResourceFactory.createIRootResource(null, new Specification(Type.OTHER), null,
				TestResourceFactory.createFakeEndpoints(), null, null);

		ArtificialBundleGuard bg = TestCapabilitiesFactory.createArtificialBundleGuard();
		ICoreModelCapability cmc = TestCapabilitiesFactory.createArtificialCoreModelCapability(resource);
		EmptyClientConfiguration ecc = TestClientProviderFactory.createEmptyClientConfiguration();

		// create APIProviderFactory, inject dummy capabilities and start it
		APIProviderFactory apf = new APIProviderFactory();
		ReflectionTestHelper.<APIProviderFactory, IBundleGuard> injectPrivateField(apf, bg, "bundleGuard");
		ReflectionTestHelper.<APIProviderFactory, ICoreModelCapability> injectPrivateField(apf, cmc, "coreModelCapability");
		apf.activate();

		// add InternalClientProvider to the system, using an artificially generated event
		bg.throwClassEntered(ProviderFactoryHelpers.getInternalClassListener(AbstractProviderFactory.class, apf), TestInternalAPIProvider.class);

		// obtain a client from client provider
		EmptyClientAPI client = apf.<EmptyClientConfiguration, TestAPIProvider> getAPIProvider(TestAPIProvider.class).getAPIClient(resource,
				EmptyClientAPI.class, ecc);
		Assert.assertTrue("Client must be an instance of EmptyClientAPI.", client instanceof EmptyClientAPI);
	}

	@Test(expected = EndpointNotFoundException.class)
	public void testFailureAPIProviderFactory() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
			EndpointNotFoundException, ProviderNotFoundException, ApplicationActivationException {

		// generate artificial objects
		IRootResource resource = TestResourceFactory.createIRootResource(null, new Specification(Type.OTHER), null,
				TestResourceFactory.createFakeEndpoints(), null, null);

		ArtificialBundleGuard bg = TestCapabilitiesFactory.createArtificialBundleGuard();
		ICoreModelCapability cmc = TestCapabilitiesFactory.createArtificialCoreModelCapability(resource);
		EmptyClientConfiguration ecc = TestClientProviderFactory.createEmptyClientConfiguration();

		// create APIProviderFactory, inject dummy capabilities and start it
		APIProviderFactory apf = new APIProviderFactory();
		ReflectionTestHelper.<APIProviderFactory, IBundleGuard> injectPrivateField(apf, bg, "bundleGuard");
		ReflectionTestHelper.<APIProviderFactory, ICoreModelCapability> injectPrivateField(apf, cmc, "coreModelCapability");
		apf.activate();

		// add InternalClientProvider to the system, using an artificially generated event
		bg.throwClassEntered(ProviderFactoryHelpers.getInternalClassListener(AbstractProviderFactory.class, apf), TestInternalAPIProvider.class);

		// obtain a client from client provider
		apf.<EmptyClientConfiguration, TestAPIProvider> getAPIProvider(TestAPIProvider.class, new IEndpointSelectionStrategy() {
			@Override
			public Endpoint select(String[] protocols, Collection<Endpoint> endpoints) {
				// force null return to produce an EndpointNotFoundException
				return null;
			}
		}).getAPIClient(resource, EmptyClientAPI.class, ecc);
	}
}
