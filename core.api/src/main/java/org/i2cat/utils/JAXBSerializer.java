package org.i2cat.utils;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.SerializationException;

/**
 * A utility class that serializes Java object models to and from XML using JAXB
 * 
 * @author Scott Campbell (CRC)
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class JAXBSerializer {

	public static String toXml(Object obj) throws SerializationException {
		StringWriter sw = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller m = context.createMarshaller();

			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(obj, sw);
			return sw.toString();
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Deserializes an XML String
	 * 
	 * @param xml
	 * @return
	 */
	public static Object fromXml(String xml, String packageName) throws SerializationException {

		StringReader in = new StringReader(xml);
		try {
			JAXBContext context = JAXBContext.newInstance(packageName);
			Object obj = context
					.createUnmarshaller().unmarshal(in);
			return obj;
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Deserialize the XML String into an instance of the provided class
	 * 
	 * @param xml
	 * @param objectClass
	 * @return
	 * @throws SerializationException
	 */
	public static <T> T fromXml(String xml, Class<T> objectClass) throws SerializationException {
		return fromXml(new ByteArrayInputStream(xml.getBytes()), objectClass);
	}

	/**
	 * Deserialize the XML InputStream into an instance of provided class
	 * 
	 * @param xml
	 * @param objectClass
	 * @return
	 * @throws SerializationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(InputStream xml, Class<T> objectClass) throws SerializationException {
		try {
			JAXBContext context = JAXBContext.newInstance(objectClass);
			T obj = (T) context
					.createUnmarshaller().unmarshal(xml);
			return obj;
		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

	/**
	 * Deserialize the XML String into a List of instances of provided class
	 * 
	 * @param xml
	 * @param clazz
	 * @return
	 * @throws SerializationException
	 */
	public static <T> List<T> listFromXml(String xml, Class<T> clazz) throws SerializationException {
		return listFromXml(new ByteArrayInputStream(xml.getBytes()), clazz);
	}

	public static <T> List<T> listFromXml(InputStream xml, Class<T> clazz) throws SerializationException {

		JAXBContext context;
		try {
			context = JAXBContext.newInstance(GenericListWrapper.class, clazz);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			@SuppressWarnings("unchecked")
			GenericListWrapper<T> wrapper = (GenericListWrapper<T>) unmarshaller.unmarshal(new StreamSource(xml), GenericListWrapper.class)
					.getValue();

			return wrapper.getItems();

		} catch (JAXBException e) {
			throw new SerializationException(e);
		}
	}

}
