package org.mqnaas.extensions.odl.client.switchnorthbound.api.adapter;

/*
 * #%L
 * MQNaaS :: ODL Client
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mqnaas.extensions.odl.client.switchnorthbound.api.PropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * {@link XmlAdapter} for {@literal Map<String, PropertyValue>}.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class PropertiesMapAdapter extends XmlAdapter<Object, Map<String, PropertyValue>> {

	private static final Logger	log	= LoggerFactory.getLogger(PropertiesMapAdapter.class);

	private DocumentBuilder		documentBuilder;

	public PropertiesMapAdapter() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			documentBuilder = dbf.newDocumentBuilder();
		} catch (Exception e) {
			log.error("Error creating DocumentBuilder.", e);
		}
	}

	@Override
	public Map<String, PropertyValue> unmarshal(Object v) throws Exception {
		Map<String, PropertyValue> map = new HashMap<String, PropertyValue>();

		// root element "properties"
		Element element = (Element) v;
		if (!element.getTagName().equals("properties")) {
			throw new Exception("\"properties\" tag expected, found " + element.getTagName());
		}

		// iterate over each property
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i) instanceof Element) {
				Element childElement = (Element) childNodes.item(i);
				if (childElement.getTagName().equals("property")) {
					// extract value and optional name
					String value = null, name = null;
					NodeList valueAndNameNodes = childElement.getChildNodes();

					// ignore empty child
					if (valueAndNameNodes.getLength() == 0) {
						continue;
					}

					for (int j = 0; j < valueAndNameNodes.getLength(); j++) {
						if (valueAndNameNodes.item(j) instanceof Element) {
							Element childValueOrNameNode = (Element) valueAndNameNodes.item(j);
							if (childValueOrNameNode.getTagName().equals("value")) {
								value = childValueOrNameNode.getTextContent();
							} else if (childValueOrNameNode.getTagName().equals("name")) {
								name = childValueOrNameNode.getTextContent();
							}
						}
					}

					if (value == null) {
						throw new Exception("\"value\" tag not found.");
					}

					String propertyName = childElement.getAttributeNodeNS("http://www.w3.org/2001/XMLSchema-instance", "type").getValue();
					if (propertyName == null) {
						throw new Exception("Property type attribute not found.");
					}

					// set value and name
					map.put(propertyName, new PropertyValue(value, name));
				}
			}
		}

		return map;
	}

	@Override
	public Object marshal(Map<String, PropertyValue> v) throws Exception {
		Document document = documentBuilder.newDocument();
		Element root = document.createElement("properties");
		for (Entry<String, PropertyValue> entry : v.entrySet()) {
			Element mapEntryElement = document.createElement("property");

			// value
			if (entry.getValue().getValue() == null) {
				throw new Exception("\"value\" not found.");
			}
			mapEntryElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type", entry.getKey());
			Element valueElement = document.createElement("value");
			valueElement.setTextContent(entry.getValue().getValue());
			mapEntryElement.appendChild(valueElement);

			// name if present
			if (entry.getValue().getName() != null) {
				Element nameElement = document.createElement("name");
				nameElement.setTextContent(entry.getValue().getName());
				mapEntryElement.appendChild(nameElement);
			}

			root.appendChild(mapEntryElement);
		}
		return root;
	}
}
