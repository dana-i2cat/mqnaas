package org.mqnaas.extensions.openstack.jclouds.clientprovider.internal;

/*
 * #%L
 * MQNaaS :: JClouds Client Provider
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.jclouds.ContextBuilder;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.mqnaas.clientprovider.api.client.IInternalClientProvider;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.credentials.Credentials;
import org.mqnaas.core.api.credentials.UsernamePasswordTenantCredentials;

/**
 * jClouds OpenStack Neutron {@link IInternalClientProvider}. No configuration allowed.
 * 
 * @author Julio Carlos Barrera (i2CAT Foundation)
 *
 */
public class InternalJCloudsNeutronClientProvider implements IInternalClientProvider<NeutronApi, Object> {

	@Override
	public String[] getProtocols() {
		return new String[] { "http", "https" };
	}

	@Override
	public NeutronApi getClient(Endpoint ep, Credentials c) {
		if (!(c instanceof UsernamePasswordTenantCredentials)) {
			throw new IllegalStateException(
					"InternalJCloudsNeutronClientProvider requires UsernamePasswordTenantCredentials! Provided: " + c.getClass());
		}

		String userName = ((UsernamePasswordTenantCredentials) c).getUserName();
		String password = ((UsernamePasswordTenantCredentials) c).getPassword();
		String tenant = ((UsernamePasswordTenantCredentials) c).getTenant();

		if (ep == null || ep.getUri() == null) {
			throw new IllegalStateException("InternalJCloudsNeutronClientProvider requires a valid endpoint URI! Provided: " + ep);
		}

		String uri = (String) ep.getUri().toString();

		return ContextBuilder.newBuilder("openstack-neutron").endpoint(uri).credentials(tenant + ":" + userName, password).buildApi(NeutronApi.class);
	}

	@Override
	public NeutronApi getClient(Endpoint ep, Credentials c, Object configuration) {
		return getClient(ep, c);
	}

}
