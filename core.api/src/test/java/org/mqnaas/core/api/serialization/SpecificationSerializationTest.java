package org.mqnaas.core.api.serialization;

/*
 * #%L
 * MQNaaS :: Core.API
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class SpecificationSerializationTest {

	private final static String	SPEC_FILE		= "/serialization/spec/spec.xml";
	private final static String	VERSION			= "1.0";
	private static final String	MODEL			= "Juniper";

	@Test
	public void specificationSerializationTest() throws JAXBException, SAXException, IOException {

		Specification spec = generateSampleSpec();

		String serializedXml = SerializationUtils.toXml(spec);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(SPEC_FILE));

		XMLAssert.assertXMLEqual("Serialized xml should be equals to the expected one.", expectedXml, serializedXml);

	}

	@Test
	public void specificationDeserializationTest() throws IOException, JAXBException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(SPEC_FILE));
		Specification deserializedSpec = SerializationUtils.fromXml(xml, Specification.class);
		Specification expectedSpec = generateSampleSpec();

		Assert.assertEquals("Deserialized Specification should be equals as the sample one.", expectedSpec, deserializedSpec);

	}

	private Specification generateSampleSpec() {
		return new Specification(Type.SWITCH, MODEL, VERSION);
	}
}
