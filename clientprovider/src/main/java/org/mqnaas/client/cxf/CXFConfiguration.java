package org.mqnaas.client.cxf;

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

import java.util.ArrayList;
import java.util.List;

public class CXFConfiguration {

	private boolean			useDummyClient;
	private List<Object>	providers;
	private boolean			checkCN;
	private boolean			useAsyncHttpConduit;

	public CXFConfiguration() {
		providers = new ArrayList<Object>();
	}

	/**
	 * Specifies whether a dummy client is desired or not. A dummy client is not a valid client and does not perform any other activity than logging.
	 * 
	 * @return
	 */
	public boolean getUseDummyClient() {
		return useDummyClient;
	}

	/**
	 * Specifies whether a dummy client is desired or not. A dummy client is not a valid client and does not perform any other activity than logging.
	 * 
	 * @param useDummyClient
	 */
	public void setUseDummyClient(boolean useDummyClient) {
		this.useDummyClient = useDummyClient;
	}

	/**
	 * Returns the list of customs JAX-RS providers of this configuration.
	 * 
	 * @return Collection of JAX-RS providers.
	 */
	public List<Object> getProviders() {
		return providers;
	}

	/**
	 * Set a list of JAX-RS provider to this CXFConfiguration. It would be used by the {@link InternalCXFClientProvider} as custom provider for
	 * serializing/deserializing data.
	 * 
	 * @param providers
	 */
	public void setProviders(List<Object> providers) {
		this.providers = providers;
	}

	/**
	 * @return <code>true</code> if CXF client is using Asynchronous HTTP transport. <code>false</code> otherwise.
	 */
	public boolean isUsingAsyncHttpConduit() {
		return useAsyncHttpConduit;
	}

	/**
	 * Specifies whether the CXF client should use the Asynchronous HTTP transport.
	 * <p>
	 * By enabling async http conduit, as side-effect, support for @Delete methods with body is available.
	 * </p>
	 * <p>
	 * https://issues.apache.org/jira/browse/CXF-5337
	 * </p>
	 */
	public void setUseAsyncHttpConduit(boolean useAsyncHttpConduit) {
		this.useAsyncHttpConduit = useAsyncHttpConduit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CXFConfiguration [useDummyClient=" + useDummyClient + "]";
	}

	/**
	 * Returns whether or not JSSE omits checking if the host name specified in the URL matches that of the Common Name (CN) on the server's
	 * certificate.
	 */
	public boolean isCNCheckEnabled() {
		return checkCN;
	}

	/**
	 * Specifies whether or not JSSE should omit checking if the host name specified in the URL matches that of the Common Name (CN) on the server's
	 * certificate.
	 */
	public void setCheckCN(boolean checkCN) {
		this.checkCN = checkCN;
	}

}
