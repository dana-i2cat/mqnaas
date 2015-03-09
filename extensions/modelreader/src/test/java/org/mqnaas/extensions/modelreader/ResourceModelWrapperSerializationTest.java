package org.mqnaas.extensions.modelreader;

/*
 * #%L
 * MQNaaS :: Network API
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
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.core.api.Specification;
import org.mqnaas.extensions.modelreader.api.ResourceModelWrapper;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.xml.sax.SAXException;

/**
 * <p>
 * Class containing test for serialization and deserialization of the {@link ResourceModelWrapper} class.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ResourceModelWrapperSerializationTest {

	private final static String	NETWORK_RESOURCE_ID	= "network-1";
	private final static String	SWITCH_RESOURCE_ID	= "ofswitch-2";
	private static final String	PORT1_RESOURCE_ID	= "port-1";
	private static final String	PORT2_RESOURCE_ID	= "port-2";
	private static final String	PORT1_EXTERNAL_ID	= "eth0";
	private static final String	PORT2_EXTERNAL_ID	= "eth1";

	private final static String	RESULT_FILE			= "/serialization/resourceModelWrapper.xml";

	@Test
	public void serializationTest() throws JAXBException, SAXException, IOException {
		ResourceModelWrapper resourceModelWrapper = generateSampleResourceModelWrapper();

		String serializedXml = SerializationUtils.toXml(resourceModelWrapper);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(RESULT_FILE));

		XMLAssert.assertXMLEqual("Serialized xml should be equals to the expected one.", expectedXml, serializedXml);

	}

	@Test
	public void deserializationTest() throws IOException, JAXBException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(RESULT_FILE));
		ResourceModelWrapper deserializedModel = SerializationUtils.fromXml(xml, ResourceModelWrapper.class);
		ResourceModelWrapper expectedModel = generateSampleResourceModelWrapper();

		Assert.assertEquals("Deserialized resource model should be equals to the sample one.", expectedModel, deserializedModel);

	}

	private ResourceModelWrapper generateSampleResourceModelWrapper() {

		ResourceModelWrapper switchPort1 = new ResourceModelWrapper(PORT1_RESOURCE_ID);
		switchPort1.setType("port");
		switchPort1.setExternalId(PORT1_EXTERNAL_ID);

		ResourceModelWrapper switchPort2 = new ResourceModelWrapper(PORT2_RESOURCE_ID);
		switchPort2.setType("port");
		switchPort2.setExternalId(PORT2_EXTERNAL_ID);

		ResourceModelWrapper switchModel = new ResourceModelWrapper(SWITCH_RESOURCE_ID);
		switchModel.setType(Specification.Type.OF_SWITCH.toString());
		switchModel.setResources(Arrays.asList(switchPort1, switchPort2));

		ResourceModelWrapper networkModel = new ResourceModelWrapper(NETWORK_RESOURCE_ID);
		networkModel.setType(Specification.Type.NETWORK.toString());
		networkModel.getResources().add(switchModel);

		return networkModel;
	}

}
