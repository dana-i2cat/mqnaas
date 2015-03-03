package org.mqnaas.api.translators;

/*
 * #%L
 * MQNaaS :: REST API Provider
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

	@Override
	public String toString() {
		return "Simple ID to Resource translator";
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