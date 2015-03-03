package test;

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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.mqnaas.client.application.ApplicationConfiguration;
import org.mqnaas.client.application.IApplicationClient;
import org.mqnaas.client.cxf.CXFConfiguration;
import org.mqnaas.client.cxf.ICXFAPIProvider;
import org.mqnaas.client.netconf.INetconfClientProvider;
import org.mqnaas.client.netconf.NetconfClient;
import org.mqnaas.client.netconf.NetconfConfiguration;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProviderFactory;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME This test application should be moved to a unit test.
 * 
 */
public class ClientApplication /*implements IApplication*/ {

	private static final Logger	log	= LoggerFactory.getLogger(ClientApplication.class);

	@DependingOn
	IRootResourceAdministration	rootResourceManagement;

	@DependingOn
	IClientProviderFactory		clientProviderFactory;

	@DependingOn
	IAPIClientProviderFactory	apiProviderFactory;

//	@Override
	public void activate() {

		log.info("Running the Client test application...");

		// Fake resource
		IRootResource resource;
		try {
			resource = rootResourceManagement.createRootResource(RootResourceDescriptor.create(new Specification(Type.OTHER),
					Arrays.asList(new Endpoint(new URI("ssh://localhost/")), new Endpoint(new URI("http://localhost/")))));
		} catch (URISyntaxException e) {
			log.error("Error creating SSH URI", e);
			return;
		} catch (InstantiationException e) {
			log.error("Error creating resource", e);
			return;
		} catch (IllegalAccessException e) {
			log.error("Error creating resource", e);
			return;
		}

		try {
			// 1. Static client provisioning
			INetconfClientProvider cp = clientProviderFactory.getClientProvider(INetconfClientProvider.class);

			// Client w/o configuration
			NetconfClient netconfClient1 = cp.getClient(resource);
			netconfClient1.doNetconfSpecificThing1();

			// Client with (client specific) configuration
			NetconfConfiguration netconfConfiguration = new NetconfConfiguration();

			NetconfClient netconfClient2 = cp.getClient(resource, netconfConfiguration);
			netconfClient2.doNetconfSpecificThing2();

			// 2. Dynamic client provisioning
			ICXFAPIProvider ap = apiProviderFactory.getAPIProvider(ICXFAPIProvider.class);

			// Dynamic client w/o configuration
			IApplicationClient applicationSpecificClient1 = ap.getAPIClient(resource, IApplicationClient.class);
			applicationSpecificClient1.methodA();
			applicationSpecificClient1.methodB();

			// Dynamic client with (client specific) configuration
			CXFConfiguration cxfConf = new CXFConfiguration();
			cxfConf.setUseDummyClient(true);
			IApplicationClient applicationSpecificClient2 = ap.getAPIClient(resource, IApplicationClient.class, cxfConf);
			applicationSpecificClient2.methodA();
			applicationSpecificClient2.methodB();

			// Dynamic client with client specific configuration and application
			// specific configuration
			CXFConfiguration cxfConf2 = new CXFConfiguration();
			cxfConf2.setUseDummyClient(true);
			IApplicationClient applicationSpecificClient3 = ap.getAPIClient(resource, IApplicationClient.class, cxfConf2,
					new ApplicationConfiguration());
			applicationSpecificClient3.methodA();
			applicationSpecificClient3.methodB();
		} catch (Exception e) {
			log.error("Error obtaining client provider or invoking client.", e);
		}
	}

//	@Override
	public void deactivate() {
		// nothing to do
	}

}
