package org.mqnaas.general.test.helpers.serialization;

/*
 * #%L
 * MQNaaS :: General Test Helpers
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

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SerializationUtils {

	public static String toXml(Object obj) throws JAXBException {
		StringWriter sw = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(obj.getClass());
		Marshaller m = context.createMarshaller();

		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(obj, sw);
		return sw.toString();
	}

	/**
	 * Deserialize the XML InputStream into an instance of provided class
	 * 
	 * @param xml
	 * @param objectClass
	 * @return
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(InputStream xml, Class<T> objectClass) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(objectClass);
		T obj = (T) context
				.createUnmarshaller().unmarshal(xml);
		return obj;
	}

	/**
	 * Deserialize the XML String into an instance of provided class
	 * 
	 * @param xml
	 * @param objectClass
	 * @return
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, Class<T> objectClass) throws JAXBException {
		StringReader in = new StringReader(xml);
		JAXBContext context = JAXBContext.newInstance(objectClass);
		T obj = (T) context
				.createUnmarshaller().unmarshal(in);
		return obj;
	}

}
