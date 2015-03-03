package org.mqnaas.test.helpers.clientprovider;

/*
 * #%L
 * MQNaaS :: MQNaaS Test Helpers
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
 * {@link IInternalClientProvider} for testing purposes able to return always a new {@link EmptyClient}.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TestInternalClientProvider implements IInternalClientProvider<EmptyClient, EmptyClientConfiguration> {

	@Override
	public String[] getProtocols() {
		return new String[] { "protocol" };
	}

	@Override
	public EmptyClient getClient(Endpoint ep, Credentials c) {
		return new EmptyClient();
	}

	@Override
	public EmptyClient getClient(Endpoint ep, Credentials c, EmptyClientConfiguration configuration) {
		return new EmptyClient(c);
	}

}