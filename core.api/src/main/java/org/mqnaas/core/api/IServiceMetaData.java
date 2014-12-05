package org.mqnaas.core.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * <code>IServiceMetaData</code> represents data associated with an {@link IService}
 * 
 */
public interface IServiceMetaData {
	
	/**
	 * 
	 * @return the method in IApplication representing implementing this service
	 */
	public Method getMethod();

	/**
	 * 
	 * @return the IApplication instance offering this service 
	 */
	public IApplication getApplication();

	/**
	 * 
	 * @return the class this service belongs to
	 */
	public Class<? extends IApplication> getApplicationClass();
	
	/**
	 * 
	 * @return the IApplication interface this service belongs to
	 */
	public Class<? extends IApplication> getApplicationInterface();

	/**
	 * 
	 * @return the service name
	 */
	public String getName();

	/**
	 * 
	 * @return {@link Annotation}s present in this service method
	 */
	public Annotation[] getAnnotations();

	/**
	 * 
	 * @return parameter types of given service
	 */
	public Class<?>[] getParameterTypes();

	/**
	 * 
	 * @param annotation to ask for
	 * @return true if this service has given annotation, false otherwise.
	 */
	public boolean hasAnnotation(Class<? extends Annotation> annotation);
}
