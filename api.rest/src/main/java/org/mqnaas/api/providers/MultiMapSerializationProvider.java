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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.mqnaas.core.api.IIdentifiable;

import com.google.common.collect.Multimap;

/**
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */

public class MultiMapSerializationProvider extends AbstractMessageBodyWriter<Object> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

		boolean isWriteable = Multimap.class.isAssignableFrom(type);

		if (isWriteable && genericType instanceof ParameterizedType) {
			// TODO How can we configure custom serializers? Type information is not available at runtime
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			Type[] arguments = parameterizedType.getActualTypeArguments();
			isWriteable = arguments[0].equals(Class.class) && arguments[1].equals(IIdentifiable.class);
		}

		return isWriteable;
	}

	@Override
	public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {

		StringBuilder sb = new StringBuilder();

		Multimap<Class<?>, IIdentifiable> multimap = (Multimap<Class<?>, IIdentifiable>) t;

		sb.append("<services>");

		for (Class<?> capability : multimap.keySet()) {
			sb.append("<capability name=\"").append(capability.getName()).append("\">");

			for (IIdentifiable identifiable : multimap.get(capability)) {

				sb.append("<service>");
				sb.append(identifiable.getId());
				sb.append("</service>");
			}

			sb.append("</capability>");
		}

		sb.append("</services>");

		entityStream.write(sb.toString().getBytes());
	}
}
