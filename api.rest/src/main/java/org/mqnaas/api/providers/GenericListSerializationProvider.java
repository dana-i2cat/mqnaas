package org.mqnaas.api.providers;

/*
 * #%L
 * MQNaaS :: REST API Provider
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
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.mqnaas.api.ContentType;
import org.mqnaas.api.writers.InterfaceWriter;
import org.mqnaas.core.api.IIdentifiable;
import org.mqnaas.core.api.annotations.ListsResources;

/**
 * Serializes lists of objects that are returned by service method annotated with the {@link ContentType} annotation. This construction only works in
 * cooperation with the {@link InterfaceWriter}, which adds this annotation to the all list services, e.g. those annotated with {@link ListsResources}
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public class GenericListSerializationProvider extends AbstractMessageBodyWriter<Object> {

	private Annotation getContentTypeAnnotation(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == ContentType.class)
				return annotation;
		}

		return null;
	}

	@Override
	public boolean isWriteable(Class<?> type, java.lang.reflect.Type genericType, Annotation[] annotations, MediaType mediaType) {
		return List.class.isAssignableFrom(type) && getContentTypeAnnotation(annotations) != null;
	}

	@Override
	public void writeTo(Object t, Class<?> type, java.lang.reflect.Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

		ContentType contentTypeAnnotation = (ContentType) getContentTypeAnnotation(annotations);

		Class<?> contentType = contentTypeAnnotation.value();

		// TODO Rewrite this implementation
		String name = contentType.getSimpleName();
		String nameId = name + "Id";

		StringBuilder sb = new StringBuilder();

		sb.append("<").append(name).append(">");

		List<?> list = (List<?>) t;

		for (Object value : list) {
			IIdentifiable id = (IIdentifiable) value;

			sb.append("<").append(nameId).append(">");
			sb.append(id.getId());
			sb.append("</").append(nameId).append(">");
		}

		sb.append("</").append(name).append(">");

		entityStream.write(sb.toString().getBytes());
	}
}
