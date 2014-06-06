package org.mqnaas.core.impl;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.mqnaas.core.api.IBindingDecider;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinderDecider implements IBindingDecider {

	private static final Logger	log	= LoggerFactory.getLogger(BinderDecider.class);

	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Specification.Type.CORE;
	}

	private static final String	IS_SUPPORTING_METHOD_NAME	= "isSupporting";

	@Override
	public boolean shouldBeBound(IResource resource, Class<? extends ICapability> capabilityClass) {

		boolean shouldBeBound = false;

		List<Class<?>> interfaces = ClassUtils.getAllInterfaces(capabilityClass);
		interfaces.remove(ICapability.class);

		// If their is no interface remaining, there's nothing to bind...
		if (interfaces.isEmpty())
			return shouldBeBound;

		// Now for the process of binding, which for the moment is a very simple implementation: look for a static isSupporting method in the
		// capability and use it to determine the binding
		try {
			Method isSupportingMethod = capabilityClass.getMethod(IS_SUPPORTING_METHOD_NAME, IResource.class);
			shouldBeBound = (Boolean) isSupportingMethod.invoke(null, resource);
		} catch (Exception e1) {
			if (resource instanceof IRootResource) {
				try {
					Method isSupportingMethod = capabilityClass.getMethod(IS_SUPPORTING_METHOD_NAME, IRootResource.class);
					shouldBeBound = (Boolean) isSupportingMethod.invoke(null, resource);
				} catch (Exception e2) {
					// no way to establish bind
					log.error(
							"No way of establishing bind with Capability " + capabilityClass.getName() + ". No isSupporting(...) implementation found.",
							e2);
				}
			}
		}

		log.debug(getClass().getSimpleName() + ".shouldBeBound(" + resource + ", " + capabilityClass + "): " + shouldBeBound);

		return shouldBeBound;
	}
}
