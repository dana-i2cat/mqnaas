package org.mqnaas.client.cxf;

import java.util.ArrayList;
import java.util.List;

import org.mqnaas.client.cxf.Authentication;

public class CXFConfiguration {

	private boolean			useDummyClient;
	private List<Object>	providers;
	private boolean			checkCN;
	private boolean			useAsyncHttpConduit;
	private Authentication	authentication;

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

	/**
	 * Returns the {@link Authentication} system and data being used.
	 * 
	 * @return
	 */
	public Authentication getAuthentication() {
		return authentication;
	}

	/**
	 * Sets the {@link Authentication} system and data to be used by CXF.
	 * 
	 * @param authentication
	 *            Authentication information to be attached to the CXF client.
	 */
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

}
