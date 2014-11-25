package org.mqnaas.core.api.slicing.serialization;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.core.api.slicing.SliceUnit;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class SlicingUnitSerializationTest {

	private final static String	SLICE_UNIT_NAME	= "vlan";
	private final static String	SLICE_FILE_1	= "/serialization/slice/slice.xml";

	@Test
	public void sliceUnitSerializationTest() throws JAXBException, IOException, SAXException {

		SliceUnit su = new SliceUnit(SLICE_UNIT_NAME);

		String serializedXml = SerializationUtils.toXml(su);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(SLICE_FILE_1));

		XMLAssert.assertXMLEqual("Serialized xml should be equals to the expected one.", expectedXml, serializedXml);
	}

	@Test
	public void sliceUnitDeserializationTest() throws IOException, JAXBException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(SLICE_FILE_1));
		SliceUnit deserializedSliceUnit = SerializationUtils.fromXml(xml, SliceUnit.class);
		SliceUnit expectedSliceUnit = new SliceUnit(SLICE_UNIT_NAME);

		Assert.assertEquals("Deserialized Slice Unit should be equals as the sample one.", expectedSliceUnit, deserializedSliceUnit);

	}

}
