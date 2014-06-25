package org.mqnaas.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IServiceMetaData;

/**
 * {@link IServiceMetaData} implementation
 * 
 */
public class ServiceMetaData implements IServiceMetaData {

	Method			method;

	IApplication	application;

	ServiceMetaData(Method method, IApplication application) {
		this.method = method;
		this.application = application;
	}

	public Method getMethod() {
		return method;
	}

	public IApplication getApplication() {
		return application;
	}

	public Class<? extends IApplication> getApplicationClass() {
		return application.getClass();
	}

	public String getName() {
		return method.getName();
	}

	public Annotation[] getAnnotations() {
		return method.getAnnotations();
	}

	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}

	public boolean hasAnnotation(Class<? extends Annotation> annotation) {
		return method.isAnnotationPresent(annotation);
	}
}
