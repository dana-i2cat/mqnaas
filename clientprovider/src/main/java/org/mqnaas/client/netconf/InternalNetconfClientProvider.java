package org.mqnaas.client.netconf;

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

import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.credentials.Credentials;

/**
 * This is an example implementation of how to implement a specific client provider
 */
public class InternalNetconfClientProvider implements IInternalClientProvider<NetconfClient, NetconfConfiguration> {

	@Override
	public String[] getProtocols() {
		// Netconf endpoints using SSH as transport layer
		return new String[] { "ssh" };
	}

	@Override
	public NetconfClient getClient(Endpoint ep, Credentials c) {
		NetconfConfiguration defaultConfiguration = null;
		return getClient(ep, c, defaultConfiguration);
	}

	@Override
	public NetconfClient getClient(Endpoint ep, Credentials c, NetconfConfiguration configuration) {
		return new NetconfClient(ep, c, configuration);
	}

}
