package org.mqnaas.core.api.slicing.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.slicing.SliceUnit;
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
	private final static String	SLICE_UNIT_NAME	= "vlan";

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

		SliceUnit su = new SliceUnit(SLICE_UNIT_NAME);

		List<SliceUnit> sliceUnits = new ArrayList<SliceUnit>();
		sliceUnits.add(su);

		Specification spec = new Specification();
		spec.setType(Type.SWITCH);
		spec.setVersion(VERSION);
		spec.setModel(MODEL);
		spec.setSliceUnits(sliceUnits);

		return spec;

	}
}
