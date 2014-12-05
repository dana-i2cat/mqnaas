package org.mqnaas.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service implements IInternalService {
	
	private static final Logger					log	= LoggerFactory.getLogger(Service.class);

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
	public Object execute(Object[] parameters) {

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
		} catch (InvocationTargetException e) {
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
