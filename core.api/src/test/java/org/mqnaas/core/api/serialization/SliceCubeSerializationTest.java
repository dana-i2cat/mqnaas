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
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.api.slicing.Cube;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class SliceCubeSerializationTest {

	private final static String	SLICE_CUBE_FILE_1	= "/serialization/slice/cube.xml";

	@Test
	public void sliceCubeSerializationTest() throws JAXBException, SAXException, IOException {

		Cube cube = generateSampleSliceCube();

		String serializedXml = SerializationUtils.toXml(cube);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(SLICE_CUBE_FILE_1));

		XMLAssert.assertXMLEqual("Serialized xml should be equals to the expected one.", expectedXml, serializedXml);

	}

	@Test
	public void sliceCubeDeserializationTest() throws JAXBException, IOException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(SLICE_CUBE_FILE_1));

		Cube deserializedSliceCube = SerializationUtils.fromXml(xml, Cube.class);
		Cube expectedSliceCube = generateSampleSliceCube();

		Assert.assertEquals("Deserialized Slice Cube should be equals as the sample one.", expectedSliceCube, deserializedSliceCube);

	}

	private Cube generateSampleSliceCube() {

		Range[] ranges = new Range[2];
		ranges[0] = new Range(0, 12);
		ranges[1] = new Range(24, 28);

		return new Cube(ranges);

	}
}
