package org.mqnaas.client.cxf;

public class CXFConfiguration {

	private boolean	useDummyClient;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CXFConfiguration [useDummyClient=" + useDummyClient + "]";
	}

}
