package org.mqnaas.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceMetaData;

public class Service implements IInternalService {

	// The resource
	private IResource			resource;

	// The reflected method
	private IServiceMetaData	metaData;

	Service(Method method, IApplication instance) {
		metaData = new ServiceMetaData(method, instance);
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
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
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
