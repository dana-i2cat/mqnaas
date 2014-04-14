package org.opennaas.core.client.cxf;

import org.opennaas.core.clientprovider.IInternalASClientProvider;
import org.opennaas.core.other.Credentials;
import org.opennaas.core.other.Endpoint;

public class InternalCXFClientProvider implements IInternalASClientProvider {

	@Override
	public <T> T getClient(Endpoint ep, Credentials c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T, C extends T> C getClient(Class<C> clazz, Endpoint ep,
			Credentials c) {
		// TODO Auto-generated method stub
		return null;
	}


}
