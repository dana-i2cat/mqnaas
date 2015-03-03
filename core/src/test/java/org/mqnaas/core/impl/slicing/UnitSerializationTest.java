package org.mqnaas.core.impl.slicing;

/*
 * #%L
 * MQNaaS :: Core
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
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.general.test.helpers.serialization.SerializationUtils;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class UnitSerializationTest {

	private final static String	SLICE_UNIT_NAME	= "vlan";
	private final static String	SLICE_FILE_1	= "/serialization/slice/slice.xml";

	@Test
	public void sliceUnitSerializationTest() throws JAXBException, IOException, SAXException {

		UnitResource su = new UnitResource(SLICE_UNIT_NAME);

		String serializedXml = SerializationUtils.toXml(su);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(SLICE_FILE_1));

		DifferenceListener idComparingDifferenceListener = new IDComparingDifferenceListener();
		Diff myDiff = new Diff(expectedXml, serializedXml);
		myDiff.overrideDifferenceListener(idComparingDifferenceListener);

		Assert.assertTrue("Serialized xml should be equals to the expected one.", myDiff.similar());
	}

	@Test
	public void sliceUnitDeserializationTest() throws IOException, JAXBException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(SLICE_FILE_1));
		UnitResource deserializedSliceUnit = SerializationUtils.fromXml(xml, UnitResource.class);
		UnitResource expectedSliceUnit = new UnitResource(SLICE_UNIT_NAME);

		Assert.assertTrue("Deserialized Slice Unit should be equal to the sample one.", expectedSliceUnit.matches(deserializedSliceUnit));
	}

	private class IDComparingDifferenceListener implements DifferenceListener {

		private String getPrefix(String s) {
			int i = s.indexOf("-");
			return i >= 0 ? s.substring(0, i) : s;
		}

		@Override
		public int differenceFound(Difference difference) {

			if ("id".equals(difference.getControlNodeDetail().getNode().getParentNode().getNodeName())) {
				// Difference found in id attribute...
				String controlValue = difference.getControlNodeDetail().getValue();
				String testValue = difference.getTestNodeDetail().getValue();

				// only compare the prefix...
				if (getPrefix(controlValue).equals(getPrefix(testValue))) {
					return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
				}
			}

			return RETURN_ACCEPT_DIFFERENCE;
		}

		@Override
		public void skippedComparison(Node control, Node test) {
		}
	}

}
