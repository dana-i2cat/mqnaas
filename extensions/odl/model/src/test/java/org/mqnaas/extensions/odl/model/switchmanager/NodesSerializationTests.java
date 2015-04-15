package org.mqnaas.extensions.odl.model.switchmanager;

/*
 * #%L
 * MQNaaS :: ODL Model
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

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.extensions.odl.helium.switchmanager.model.Nodes;
import org.mqnaas.extensions.odl.helium.switchmanager.model.helpers.SwitchManagerModelHelper;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * {@link Nodes} (de)serialization tests
 * 
 * @author Julio Carlos Barrera
 *
 */
public class NodesSerializationTests {

	private static final Logger	log				= LoggerFactory.getLogger(NodesSerializationTests.class);

	private final static String	TEST_NODES_FILE	= "/serialization/test-nodes.xml";

	@Test
	public void nodesSerializationTest() throws JAXBException, SAXException, IOException {
		Nodes nodes = SwitchManagerModelHelper.generateSampleNodes();

		String serializedXml = SerializationUtils.toXml(nodes);
		log.info("Serialized XML:\n" + serializedXml);

		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(TEST_NODES_FILE));
		log.info("Expected XML:\n" + expectedXml);

		// ignore spaces
		XMLUnit.setIgnoreWhitespace(true);

		Diff diff = new Diff(expectedXml, serializedXml);
		XMLAssert.assertXMLEqual("Serialized XML must be equal to the expected one.", diff, false);
	}

	@Test
	public void nodesDeserializationTest() throws IOException, JAXBException {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream(TEST_NODES_FILE));
		Nodes deserializedNodes = SerializationUtils.fromXml(xml, Nodes.class);
		log.info("Deserialized Nodes:\n" + deserializedNodes);
		Nodes expectedNodes = SwitchManagerModelHelper.generateSampleNodes();
		log.info("Expected Nodes:\n" + expectedNodes);

		Assert.assertEquals("Deserialized nodes must be equals to the sample one.", expectedNodes, deserializedNodes);
	}

}
