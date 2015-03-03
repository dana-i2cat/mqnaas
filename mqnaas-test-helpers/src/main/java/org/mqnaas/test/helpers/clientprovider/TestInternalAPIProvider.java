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

import org.mqnaas.clientprovider.api.apiclient.IInternalAPIClientProvider;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.credentials.Credentials;

/**
 * {@link IInternalAPIClientProvider} for testing purposes able to return only {@link EmptyClientAPI}.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TestInternalAPIProvider implements IInternalAPIClientProvider<EmptyClientConfiguration> {

	@Override
	public String[] getProtocols() {
		return new String[] { "protocol" };
	}

	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c) {
		return getClient(apiClass, ep, c, null, null);
	}

	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, EmptyClientConfiguration configuration) {
		return getClient(apiClass, ep, c, configuration, null);
	}

	@SuppressWarnings("unchecked")
	public <API> API getClient(Class<API> apiClass, Endpoint ep, Credentials c, EmptyClientConfiguration configuration,
			Object applicationSpecificConfiguration) {
		if (apiClass.equals(EmptyClientAPI.class)) {
			return (API) new EmptyClientAPI() {
			};
		}
		return null;
	}

}