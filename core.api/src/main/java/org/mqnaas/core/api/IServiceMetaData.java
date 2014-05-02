package org.mqnaas.core.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * <code>IServiceMetaData</code> represents data associated with an {@link IService}
 * 
 */
public interface IServiceMetaData {
	public Method getMethod();

	public ICapability getCapability();

	public Class<? extends ICapability> getCapabilityClass();

	public String getName();

	public Annotation[] getAnnotations();

	public Class<?>[] getParameterTypes();

	public boolean hasAnnotation(Class<? extends Annotation> annotation);
}
