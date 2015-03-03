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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service implements IInternalService {

	private static final Logger	log	= LoggerFactory.getLogger(Service.class);

	// The resource
	private IResource			resource;

	// The reflected method
	private IServiceMetaData	metaData;

	Service(Method method, IApplication instance, Class<? extends IApplication> applicationInterface) {
		metaData = new ServiceMetaData(method, instance, applicationInterface);
	}

	@Override
	public IResource getResource() {
		return resource;
	}

	@Override
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	@Override
	public IServiceMetaData getMetadata() {
		return metaData;
	}

	@Override
	public Object execute(Object[] parameters) throws InvocationTargetException {

		Object result = null;

		try {
			result = metaData.getMethod().invoke(metaData.getApplication(), parameters);
			// FIXME fail gracefully and/or notify errors
		} catch (IllegalArgumentException e) {
			log.error("Failed to execute service " + metaData.getMethod().getName() + " of " + metaData.getApplication().getClass().getName()
					+ " with parameters " + parameters, e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log.error("Failed to execute service " + metaData.getMethod().getName() + " of " + metaData.getApplication().getClass().getName()
					+ " with parameters " + parameters, e);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getId() {
		return metaData.getApplication().getClass().getName() + ":" + metaData.getName();
	}

	public String toString() {
		return metaData.getName();
	}

}
