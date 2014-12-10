package org.mqnaas.network.api;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.mqnaas.network.api.request.Period;
import org.xml.sax.SAXException;

/**
 * <p>
 * Class containing test for serialization and deserialization of the {@link Period} class.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class PeriodSerializationTest {

	private final static String	RESULT_FILE	= "/serialization/period.xml";

	@Test
	public void periodSerializationTest() throws JAXBException, SAXException, IOException {
		Period period = generateSamplePeriod();

		String serializedXml = SerializationUtils.toXml(period);

		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(RESULT_FILE));

		XMLAssert.assertXMLEqual("Serialized xml should be equals to the expected one.", expectedXml, serializedXml);
	}

	@Test
	public void periodDeserializationTest() throws IOException, JAXBException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(RESULT_FILE));
		Period deserializedPeriod = SerializationUtils.fromXml(xml, Period.class);
		Period expectedPeriod = generateSamplePeriod();

		Assert.assertEquals("Deserialized period should be equals to the sample one.", expectedPeriod, deserializedPeriod);

	}

	private Period generateSamplePeriod() {

		Date startDate = new GregorianCalendar(2000, 01, 01).getTime();
		Date endDate = new GregorianCalendar(2000, 12, 31).getTime();
		Period period = new Period(startDate, endDate);

		return period;
	}

}
