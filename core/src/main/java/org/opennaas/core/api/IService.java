package org.opennaas.core.api;

import java.lang.annotation.Annotation;

/**
 * The <code>IService</code>
 */
public interface IService {

	IResource getResource();
	
	Class<? extends ICapability> getCapabilityClass();
	
	String getName();
	
	Annotation[] getAnnotations();
	
	Class<?>[] getParameterTypes();
	
	Object execute(Object[] parameters);

	boolean hasAnnotation(Class<? extends Annotation> annotation);

}
