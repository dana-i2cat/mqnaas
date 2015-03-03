package org.mqnaas.core.impl;

/*
 * #%L
 * MQNaaS :: Core
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
