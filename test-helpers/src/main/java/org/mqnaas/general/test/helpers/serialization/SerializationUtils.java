package org.mqnaas.general.test.helpers.serialization;

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
