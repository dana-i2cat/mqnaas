package org.mqnaas.client.cxf;

import java.util.ArrayList;
import java.util.List;

public class CXFConfiguration {

	private boolean			useDummyClient;
	private List<Object>	providers;
	private boolean			checkCN;

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
	public boolean isCNChecked() {
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
