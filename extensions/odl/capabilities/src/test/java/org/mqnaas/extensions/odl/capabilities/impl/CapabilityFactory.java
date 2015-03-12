package org.mqnaas.extensions.odl.capabilities.impl;

/*
 * #%L
 * MQNaaS :: ODL Capabilities
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.impl.utils.ReflectionUtils;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public class CapabilityFactory {

	private Map<Pair<IResource, Class<? extends ICapability>>, ICapability>	capabilities	= new HashMap<CapabilityFactory.Pair<IResource, Class<? extends ICapability>>, ICapability>();

	public <T extends ICapability> T getCapability(IResource resource, Class<T> capabilityClass) throws InstantiationException,
			IllegalAccessException, ApplicationActivationException {
		Pair<IResource, Class<? extends ICapability>> key = new Pair<IResource, Class<? extends ICapability>>(resource, capabilityClass);

		if (capabilities.containsKey(key)) {
			// Type-safe by construction
			@SuppressWarnings("unchecked")
			T capability = (T) capabilities.get(key);
			return capability;
		}

		T capability = createCapability(resource, capabilityClass);
		capabilities.put(key, capability);
		return capability;
	}

	private <T extends ICapability> T createCapability(IResource resource, Class<T> capabilityClass)
			throws InstantiationException, IllegalAccessException, ApplicationActivationException {

		T capability = capabilityClass.newInstance();
		injectResourceToCapability(capability, resource);
		capability.activate();

		return capability;
	}

	/**
	 * Injects resource in each field of given capability instance (including his superclasses).
	 */
	private static void injectResourceToCapability(ICapability capability, IResource resource) throws IllegalArgumentException,
			IllegalAccessException {
		Class<? extends ICapability> capabilityClass = capability.getClass();
		List<Field> resourceFields = ReflectionUtils.getAnnotationFields(capabilityClass, org.mqnaas.core.api.annotations.Resource.class);
		for (Field resourceField : resourceFields) {
			if (!resourceField.isAccessible()) {
				resourceField.setAccessible(true);
			}
			resourceField.set(capability, resource);
		}
	}

	/**
	 * 
	 * @author Isart Canyameres Gimenez (i2cat)
	 *
	 * @param <A>
	 * @param <B>
	 */
	public class Pair<A, B> {

		private A	first;
		private B	second;

		public Pair(A first, B second) {
			super();
			this.first = first;
			this.second = second;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((first == null) ? 0 : first.hashCode());
			result = prime * result + ((second == null) ? 0 : second.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Pair other = (Pair) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (first == null) {
				if (other.first != null)
					return false;
			} else if (!first.equals(other.first))
				return false;
			if (second == null) {
				if (other.second != null)
					return false;
			} else if (!second.equals(other.second))
				return false;
			return true;
		}

		private CapabilityFactory getOuterType() {
			return CapabilityFactory.this;
		}
	}
}
