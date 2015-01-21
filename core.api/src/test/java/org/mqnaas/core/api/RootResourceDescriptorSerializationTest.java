package org.mqnaas.core.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.credentials.Credentials;
import org.mqnaas.core.api.credentials.TrustoreKeystoreCredentials;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.xml.sax.SAXException;

public class RootResourceDescriptorSerializationTest {

	private final static String	RESULT_FILE					= "/serialization/descriptor/descriptor.xml";
	private final static String	VERSION						= "1.0";
	private static final String	MODEL						= "Juniper";
	private static final String	DESC_TRUSTORE_KEYSTORE_FILE	= "/serialization/descriptor/descriptorTrustoreKeystore.xml";

	@Test
	public void descriptorSerializationTest() throws JAXBException, SAXException, IOException, URISyntaxException {

		RootResourceDescriptor desc = generateSampleDesc();

		String serializedXml = SerializationUtils.toXml(desc);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(RESULT_FILE));

		XMLAssert.assertXMLEqual("Serialized xml should be equals to the expected one.", expectedXml, serializedXml);
	}

	@Test
	public void specificationDeserializationTest() throws IOException, JAXBException, URISyntaxException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(RESULT_FILE));
		RootResourceDescriptor deserializedDesc = SerializationUtils.fromXml(xml, RootResourceDescriptor.class);
		RootResourceDescriptor expectedDesc = generateSampleDesc();

		Assert.assertEquals("Deserialized descriptor should be equals to the sample one.", expectedDesc, deserializedDesc);
	}

	@Test
	public void descriptorTrustoreKeystoreCredentialsSerializationTest() throws URISyntaxException, JAXBException, IOException, SAXException {

		RootResourceDescriptor desc = generateSampleDesc();
		desc.setCredentials(generateSampleTrustoreKeystoreCredentials());

		String serializedXml = SerializationUtils.toXml(desc);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(DESC_TRUSTORE_KEYSTORE_FILE));

		XMLAssert.assertXMLEqual("Serialized xml should be equals to the expected one.", expectedXml, serializedXml);

	}

	@Test
	public void descriptorTrustoreKeystoreCredentialDeserializationTest() throws URISyntaxException, JAXBException, IOException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(DESC_TRUSTORE_KEYSTORE_FILE));
		RootResourceDescriptor deserializedDesc = SerializationUtils.fromXml(xml, RootResourceDescriptor.class);

		RootResourceDescriptor expectedDesc = generateSampleDesc();
		expectedDesc.setCredentials(generateSampleTrustoreKeystoreCredentials());

		Assert.assertEquals("Deserialized descriptor should be equals to the sample one.", expectedDesc, deserializedDesc);

	}

	private Credentials generateSampleTrustoreKeystoreCredentials() throws URISyntaxException {
		TrustoreKeystoreCredentials credentials = new TrustoreKeystoreCredentials();
		credentials.setKeystoreUri(new URI("/home/mqnaas/keystore.jks"));
		credentials.setKeystorePassword("keystorePassword");
		credentials.setTrustoreUri(new URI("/home/mqnaas/trustore.jks"));
		credentials.setTrustorePassword("trustorePassword");

		return credentials;
	}

	private RootResourceDescriptor generateSampleDesc() throws URISyntaxException {

		List<Endpoint> endpoints = new ArrayList<Endpoint>(2);
		endpoints.add(new Endpoint(new URI("http://localhost:9999/path1")));
		endpoints.add(new Endpoint(new URI("http://localhost:9999/path2")));

		RootResourceDescriptor desc = RootResourceDescriptor.create(generateSampleSpec(), endpoints);
		return desc;
	}

	private Specification generateSampleSpec() {
		return new Specification(Type.SWITCH, MODEL, VERSION);
	}

}
