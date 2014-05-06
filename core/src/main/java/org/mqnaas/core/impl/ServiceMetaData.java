package org.mqnaas.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IServiceMetaData;

/**
 * {@link IServiceMetaData} implementation
 * 
 */
public class ServiceMetaData implements IServiceMetaData {

	Method		method;

	ICapability	capability;

	ServiceMetaData(Method method, ICapability capability) {
		this.method = method;
		this.capability = capability;
	}

	public Method getMethod() {
		return method;
	}

	public ICapability getCapability() {
		return capability;
	}

	public Class<? extends ICapability> getCapabilityClass() {
		return capability.getClass();
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
