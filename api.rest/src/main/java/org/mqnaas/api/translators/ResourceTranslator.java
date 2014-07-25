package org.mqnaas.api.translators;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;

/**
 * <p>
 * A generic Translator that finds a {@link IResource} given a resource id.
 * </p>
 * 
 * <p>
 * It manages and uses a list of {@link ResourceResolver}s for its translation, which can be managed by the client.
 * </p>
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public class ResourceTranslator implements Translator {

	List<ResourceResolver>	resourceManager	= new ArrayList<ResourceResolver>();

	@Override
	public Object translate(Object input) {

		String id = (String) input;
		IResource resource = null;

		for (ResourceResolver rm : resourceManager) {
			resource = rm.resolve(id);
			if (resource != null)
				break;
		}

		return resource;
	}

	public void addResourceResolver(ResourceResolver resourceResolver) {
		resourceManager.add(resourceResolver);
	}

	public void removeResourceResolver(ResourceResolver resourceResolver) {
		resourceManager.remove(resourceResolver);
	}

	/*
	 * Internal class of the ResourceTranslator. Responsible for returning list of {@link IResource}s a specific management instance handles.
	 */
	public static class ResourceResolver {

		private ICapability	implementor;
		private Method		listMethod;

		public ResourceResolver(ICapability implementor, Method listMethod) {
			this.implementor = implementor;
			this.listMethod = listMethod;
		}

		public IResource resolve(String id) {

			for (IResource resource : getResources()) {
				if (resource.getId().equals(id))
					return resource;

			}

			return null;
		}

		public Method getResolvementMethod() {
			try {
				return getClass().getMethod("resolve", String.class);
			} catch (Exception e) {
				// If this fails, something very strange is going on.
				throw new RuntimeException(e);
			}
		}

		private List<IResource> getResources() {
			try {
				// The contract of the list method is, that it return a list of resources and has no parameters.
				// TODO Document the contract
				@SuppressWarnings("unchecked")
				List<IResource> resources = (List<IResource>) listMethod.invoke(implementor, new Object[0]);
				return resources;
			} catch (IllegalArgumentException e) {
				// TODO Rethink the handling of the exceptions caught..
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Rethink the handling of the exceptions caught..
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Rethink the handling of the exceptions caught..
				e.printStackTrace();
			}

			return Collections.emptyList();
		}
	}

}