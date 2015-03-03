package org.mqnaas.network.api;

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
