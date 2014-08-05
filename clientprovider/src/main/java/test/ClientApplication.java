package test;

import java.util.Arrays;

import org.mqnaas.client.application.ApplicationConfiguration;
import org.mqnaas.client.application.IApplicationClient;
import org.mqnaas.client.cxf.CXFConfiguration;
import org.mqnaas.client.cxf.ICXFAPIProvider;
import org.mqnaas.client.netconf.INetconfClientProvider;
import org.mqnaas.client.netconf.NetconfClient;
import org.mqnaas.client.netconf.NetconfConfiguration;
import org.mqnaas.clientprovider.api.apiclient.IAPIProviderFactory;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientApplication implements IApplication {

	private static final Logger	log	= LoggerFactory.getLogger(ClientApplication.class);

	@DependingOn
	IRootResourceManagement		rootResourceManagement;

	@DependingOn
	IClientProviderFactory		clientProviderFactory;

	@DependingOn
	IAPIProviderFactory			apiProviderFactory;

	@Override
	public void activate() {

		log.info("Running the Client test application...");

		// Fake resource
		IRootResource resource = rootResourceManagement.createRootResource(new Specification(Type.OTHER), Arrays.asList(new Endpoint()));

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
		cxfConf.setUseDummyClient(true);
		IApplicationClient applicationSpecificClient3 = ap.getAPIClient(resource, IApplicationClient.class, cxfConf2, new ApplicationConfiguration());
		applicationSpecificClient3.methodA();
		applicationSpecificClient3.methodB();
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

}
