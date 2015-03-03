package org.mqnaas.core.api;

/*
 * #%L
 * MQNaaS :: Core.API
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
