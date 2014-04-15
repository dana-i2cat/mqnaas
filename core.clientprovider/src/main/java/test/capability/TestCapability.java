package test.capability;

import org.opennaas.core.client.Dependecy;
import org.opennaas.core.client.application.IApplicationClient;
import org.opennaas.core.client.netconf.INetconfClient;
import org.opennaas.core.client.netconf.NetconfConfiguration;
import org.opennaas.core.clientprovider.IASClientProvider;
import org.opennaas.core.clientprovider.IClientProvider;
import org.opennaas.core.clientprovider.IClientProviderFactory;

public class TestCapability {

	@Dependecy
	IClientProviderFactory	cpf;

	public TestCapability() {

		IClientProvider<INetconfClient> cp = cpf.getClientProvider(INetconfClient.class);

		NetconfConfiguration c = new NetconfConfiguration();

		INetconfClient client1 = cp.getClient();
		client1.clientMethod();
		client1.netconfSpecificMethod();

		INetconfClient client2 = cp.getClient(c);
		client2.clientMethod();
		client2.netconfSpecificMethod();

		IASClientProvider<IApplicationClient> ascp = cpf.getASClientProvider(IApplicationClient.class);

		// FIXME how should this be done?
		// IApplicationClient client3 = ascp.getClient();
		// client3.methodA();
		// client3.methodB();

	}

}
