package org.mqnaas.api.providers;

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
