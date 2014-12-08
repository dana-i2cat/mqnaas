package org.mqnaas.api.providers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

import org.apache.commons.lang3.NotImplementedException;

public abstract class AbstractMessageBodyWriter<T> implements MessageBodyWriter<T> {

	@Override
	public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		throw new NotImplementedException("getSize");
	}

}
