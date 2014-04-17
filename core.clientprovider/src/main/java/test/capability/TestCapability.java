package test.capability;

import org.opennaas.core.client.Dependency;
import org.opennaas.core.client.application.ApplicationConfiguration;
import org.opennaas.core.client.application.IApplicationClient;
import org.opennaas.core.client.cxf.CXFConfiguration;
import org.opennaas.core.client.cxf.ICXFAPIProvider;
import org.opennaas.core.client.netconf.INetconfClientProvider;
import org.opennaas.core.client.netconf.NetconfClient;
import org.opennaas.core.client.netconf.NetconfConfiguration;
import org.opennaas.core.clientprovider.api.IAPIProviderFactory;
import org.opennaas.core.clientprovider.api.IClientProviderFactory;
import org.opennaas.core.clientprovider.impl.APIProviderFactory;
import org.opennaas.core.clientprovider.impl.ClientProviderFactory;

public class TestCapability {

	@Dependency
	IClientProviderFactory clientProviderFactory;

	@Dependency
	IAPIProviderFactory apiProviderFactory;

	public TestCapability() {

		// 0. This initialization step is done by the framework
		initFields();

		// 1. Static client provisioning
		INetconfClientProvider cp = clientProviderFactory
				.getClientProvider(INetconfClientProvider.class);

		// Client w/o configuration
		NetconfClient netconfClient1 = cp.getClient();
		netconfClient1.doNetconfSpecificThing1();

		// Client with (client specific) configuration
		NetconfConfiguration netconfConfiguration = new NetconfConfiguration();

		NetconfClient netconfClient2 = cp.getClient(netconfConfiguration);
		netconfClient2.doNetconfSpecificThing2();

		// 2. Dynamic client provisioning
		ICXFAPIProvider ap = apiProviderFactory
				.getAPIProvider(ICXFAPIProvider.class);
		
		// Dynamic client w/o configuration
		IApplicationClient applicationSpecificClient1 = ap
				.getClient(IApplicationClient.class);
		applicationSpecificClient1.methodA();
		applicationSpecificClient1.methodB();

		// Dynamic client with (client specific) configuration
		IApplicationClient applicationSpecificClient2 = ap
				.getClient(IApplicationClient.class,
						new CXFConfiguration().uri("U R I 1"));
		applicationSpecificClient2.methodA();
		applicationSpecificClient2.methodB();

		// Dynamic client with client specific configuration and application
		// specific configuration
		IApplicationClient applicationSpecificClient3 = ap.getClient(
				IApplicationClient.class,
				new CXFConfiguration().uri("U R I 2"),
				new ApplicationConfiguration());
		applicationSpecificClient3.methodA();
		applicationSpecificClient3.methodB();

	}

	private void initFields() {
		clientProviderFactory = new ClientProviderFactory();
		apiProviderFactory = new APIProviderFactory();
	}

	public static void main(String[] args) {
		new TestCapability();
	}

}
