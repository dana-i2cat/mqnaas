package org.mqnaas.client.cxf;

import java.net.URI;
import java.util.Arrays;

/**
 * <p>
 * {@link Authentication} based on client/server certificates stored in a keystore and/or trustore.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class CertificatesAuthentication extends Authentication {

	private URI		keyStoreUri;
	private URI		trustStoreUri;
	private char[]	keyStorePassword;
	private char[]	trustStorePassword;

	/**
	 * Gets the <b>absolute path</b> to the keystore file.
	 * 
	 * @return
	 */
	public URI getKeyStoreUri() {
		return keyStoreUri;
	}

	/**
	 * Sets the <b>absolute path</b> to the keystore file.
	 * 
	 * @param keyStoreUri
	 */
	public void setKeyStoreUri(URI keyStoreUri) {
		this.keyStoreUri = keyStoreUri;
	}

	/**
	 * Gets the <b>absolute path</b> to the truststore file.
	 * 
	 * @return
	 */
	public URI getTrustStoreUri() {
		return trustStoreUri;
	}

	/**
	 * Sets the <b>absolute path</b> to the truststore file.
	 * 
	 * @param keyStoreUri
	 */
	public void setTrustStoreUri(URI trustStoreUri) {
		this.trustStoreUri = trustStoreUri;
	}

	public char[] getKeyStorePassword() {
		return keyStorePassword;
	}

	public void setKeyStorePassword(char[] keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public char[] getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(char[] trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(keyStorePassword);
		result = prime * result + ((keyStoreUri == null) ? 0 : keyStoreUri.hashCode());
		result = prime * result + Arrays.hashCode(trustStorePassword);
		result = prime * result + ((trustStoreUri == null) ? 0 : trustStoreUri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CertificatesAuthentication other = (CertificatesAuthentication) obj;
		if (!Arrays.equals(keyStorePassword, other.keyStorePassword))
			return false;
		if (keyStoreUri == null) {
			if (other.keyStoreUri != null)
				return false;
		} else if (!keyStoreUri.equals(other.keyStoreUri))
			return false;
		if (!Arrays.equals(trustStorePassword, other.trustStorePassword))
			return false;
		if (trustStoreUri == null) {
			if (other.trustStoreUri != null)
				return false;
		} else if (!trustStoreUri.equals(other.trustStoreUri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CertificatesAuthentication [keyStoreUri=" + keyStoreUri + ", trustStoreUri=" + trustStoreUri + "]";
	}

}
