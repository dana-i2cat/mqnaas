package org.mqnaas.core.api.credentials;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(namespace = "org.mqnaas")
@XmlSeeAlso({ TrustoreKeystoreCredentials.class, UsernamePasswordCredentials.class })
public abstract class Credentials {

}
