package org.mqnaas.core.impl.notificationfilter;

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
import java.util.ArrayList;
import java.util.List;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IObservationFilter;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;

/**
 * Matches services annotated with annotation given in the constructor.
 * 
 * This filters prepares parameters for methods in {@link org.mqnaas.core.api.IResourceManagementListener}
 * 
 * By now, applications willing to react to resource creation or removal should observe services in IResourceManagementListener. They should not use
 * ResourceMonitoringFilter, as the resource may not be ready to be used.
 */
public class ResourceMonitoringFilter implements IObservationFilter {

	private Class<? extends Annotation>	classAnnotation;

	public ResourceMonitoringFilter(Class<? extends Annotation> classAnnotation) {
		this.classAnnotation = classAnnotation;
	}

	@Override
	public boolean observes(IService service, Object[] args) {
		return service.getMetadata().hasAnnotation(classAnnotation);
	}

	/**
	 * Retrieves affected IResource among service parameters and result. Retrieves IApplication managing affected IResource from the service itself.
	 * 
	 * The resource is retrieved following this algorithm: - If result implements IResource, it is taken as affected resource. If not found yet, look
	 * at observed services parameters, fist one implementing IResource is taken as the affected resource (if any) - Null otherwise
	 * 
	 * @return an array with affected resource and the IApplication managing it.
	 * 
	 *
	 */
	@Override
	public Object[] getParameters(IService service, Object[] args, Object result) {

		// retrieve resource
		IResource affectedResource = null;

		if (result != null)
			if (result instanceof IResource)
				affectedResource = (IResource) result;

		if (affectedResource == null)
			if (args != null) {
				for (Object arg : args) {
					if (arg instanceof IResource) {
						affectedResource = (IResource) arg;
						break;
					}
				}
			}

		// retrieve application holding the resource
		IApplication resourceHolder = service.getMetadata().getApplication();

		Class<? extends IApplication> resourceHolderInterface = service.getMetadata().getApplicationInterface();

		List<Object> parameters = new ArrayList<Object>(3);
		parameters.add(affectedResource);
		parameters.add(resourceHolder);
		parameters.add(resourceHolderInterface);
		return parameters.toArray();
	}

	@Override
	public String toString() {
		return "annotation == " + classAnnotation.getSimpleName();
	}

}
