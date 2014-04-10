package org.opennaas.core.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.opennaas.core.api.ICapability;
import org.opennaas.core.api.IResource;
import org.opennaas.core.api.IService;

public class Service implements IService {
	
	// The resource
	private IResource resource;
	
	// The reflected method
	private Method method;

	// The implementation
	private ICapability capability;
	
	public Service(IResource resource, ICapability capability, Method method) {
		this.resource = resource;
		this.capability = capability;
		this.method = method;
	}
	
	@Override
	public IResource getResource() {
		return resource;
	}

	@Override
	public String getName() {
		return method.getName();
	}
	
	public Class<? extends ICapability> getCapabilityClass() {
		return capability.getClass();
	}

	@Override
	public Annotation[] getAnnotations() {
		return method.getAnnotations();
	}
	
	@Override
	public boolean hasAnnotation(Class<? extends Annotation> annotation) {
		return method.getAnnotation(annotation) != null;
	}
	
	public Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}
	
	@Override
	public Object execute(Object[] parameters) {
		
		Object result = null;
		
		try {
			result = method.invoke(capability, parameters);
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
		return getName();
	}

}
