package org.mqnaas.core.impl;

import java.lang.reflect.InvocationTargetException;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IServiceMetaData;

public class Service implements IInternalService {

	// The resource
	private IResource			resource;

	// The reflected method
	private IServiceMetaData	metaData;

	Service(IResource resource, IServiceMetaData metaData) {
		this.resource = resource;
		this.metaData = metaData;
	}

	@Override
	public IResource getResource() {
		return resource;
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
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return result;
	}

	public String toString() {
		return metaData.getName();
	}

}
