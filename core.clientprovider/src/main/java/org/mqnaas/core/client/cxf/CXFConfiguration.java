package org.mqnaas.core.client.cxf;

public class CXFConfiguration {

	private String uri;

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public CXFConfiguration uri(String uri) {
		setUri(uri);
		return this;
	}
	
	public String getUri() {
		return uri;
	}
	
}
