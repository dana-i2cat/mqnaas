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
	
	Class<? extends IApplication> applicationInterface;

	ServiceMetaData(Method method, IApplication application, Class<? extends IApplication> applicationInterface) {
		this.method = method;
		this.application = application;
		this.applicationInterface = applicationInterface;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public IApplication getApplication() {
		return application;
	}

	@Override
	public Class<? extends IApplication> getApplicationClass() {
		return application.getClass();
	}
	
	@Override
	public Class<? extends IApplication> getApplicationInterface() {
		return applicationInterface;
	}

	@Override
	public String getName() {
		return method.getName();
	}

	@Override
	public Annotation[] getAnnotations() {
		return method.getAnnotations();
	}

	@Override
	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}

	@Override
	public boolean hasAnnotation(Class<? extends Annotation> annotation) {
		return method.isAnnotationPresent(annotation);
	}
}
