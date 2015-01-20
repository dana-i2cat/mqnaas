package org.mqnaas.core.api.credentials;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Credentials class based on certificates, managed by trustore and keystore.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrustoreKeystoreCredentials extends Credentials {

	private URI		keystoreUri;
	private URI		trustoreUri;
	private String	keystorePassword;
	private String	trustorePassword;

	public URI getKeystoreUri() {
		return keystoreUri;
	}

	public void setKeystoreUri(URI keystoreUri) {
		this.keystoreUri = keystoreUri;
	}

	public URI getTrustoreUri() {
		return trustoreUri;
	}

	public void setTrustoreUri(URI trustoreUri) {
		this.trustoreUri = trustoreUri;
	}

	public String getKeystorePassword() {
		return keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public String getTrustorePassword() {
		return trustorePassword;
	}

	public void setTrustorePassword(String trustorePassword) {
		this.trustorePassword = trustorePassword;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keystorePassword == null) ? 0 : keystorePassword.hashCode());
		result = prime * result + ((keystoreUri == null) ? 0 : keystoreUri.hashCode());
		result = prime * result + ((trustorePassword == null) ? 0 : trustorePassword.hashCode());
		result = prime * result + ((trustoreUri == null) ? 0 : trustoreUri.hashCode());
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
		TrustoreKeystoreCredentials other = (TrustoreKeystoreCredentials) obj;
		if (keystorePassword == null) {
			if (other.keystorePassword != null)
				return false;
		} else if (!keystorePassword.equals(other.keystorePassword))
			return false;
		if (keystoreUri == null) {
			if (other.keystoreUri != null)
				return false;
		} else if (!keystoreUri.equals(other.keystoreUri))
			return false;
		if (trustorePassword == null) {
			if (other.trustorePassword != null)
				return false;
		} else if (!trustorePassword.equals(other.trustorePassword))
			return false;
		if (trustoreUri == null) {
			if (other.trustoreUri != null)
				return false;
		} else if (!trustoreUri.equals(other.trustoreUri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TrustoreKeystoreCredentials [keystoreUri=" + keystoreUri + ", trustoreUri=" + trustoreUri + ", keystorePassword=****" + ", trustorePassword=****]";
	}

}
