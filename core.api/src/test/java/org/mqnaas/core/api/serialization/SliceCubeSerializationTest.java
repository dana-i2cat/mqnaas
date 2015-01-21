package org.mqnaas.core.api.serialization;

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
