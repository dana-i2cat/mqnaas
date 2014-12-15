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
		return resource.getDescriptor().getSpecification().getType() == Specification.Type.CORE;
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
		
		Method isSupportingForResourceMethod = getIsSupporting(capabilityClass, IResource.class);
		Method isSupportingForRootResourceMethod = getIsSupporting(capabilityClass, IRootResource.class);
		
		try {
			shouldBeBound = (Boolean) isSupportingForResourceMethod.invoke(null, resource);

			if (!shouldBeBound && (resource instanceof IRootResource)) {
				shouldBeBound = (Boolean) isSupportingForRootResourceMethod.invoke(null, resource);
			}

		} catch (Exception e1) {
			if (resource instanceof IRootResource) {
				try {
					shouldBeBound = (Boolean) isSupportingForRootResourceMethod.invoke(null, resource);
				} catch (Exception e2) {
					// no way to establish bind
					StringBuilder sb = new StringBuilder();
					sb.append("No way of establishing bind between ");
					sb.append(capabilityClass.getName()).append(" and resource ").append(resource.getClass().getSimpleName());
					
					if ( resource instanceof IRootResource ) {
						IRootResource rr = (IRootResource) resource;
						Specification specification = rr.getDescriptor().getSpecification();
						
						sb.append("[type=").append(specification.getType());
						if ( specification.getModel() != null ) {
							sb.append(", model=").append(specification.getModel());	
						}
						if (specification.getVersion() != null ) {
							sb.append(", version=").append(specification.getVersion());
						}
						sb.append("]");
					}
					
					sb.append(".");
					
					if ( isSupportingForResourceMethod != null ) {
						sb.append(" Tried ").append(IS_SUPPORTING_METHOD_NAME).append("(").append(IResource.class.getSimpleName()).append(").");
					} else if ( isSupportingForRootResourceMethod != null ) {
						sb.append(" Tried ").append(IS_SUPPORTING_METHOD_NAME).append("(").append(IRootResource.class.getSimpleName()).append(").");
					} else {
						sb.append(" No ").append(IS_SUPPORTING_METHOD_NAME).append("(...) implementation found.");
					}
					
					log.info(sb.toString());
				}
			}
		}

		log.debug(getClass().getSimpleName() + ".shouldBeBound(" + resource + ", " + capabilityClass + "): " + shouldBeBound);

		return shouldBeBound;
	}
	
	private Method getIsSupporting(Class<? extends ICapability> capabilityClass, Class<?> parameter) {
		
		Method m = null;
		
		try {
			m = capabilityClass.getMethod(IS_SUPPORTING_METHOD_NAME, parameter);
		} catch (NoSuchMethodException e) {
			// Ignore silently. Method does not exist
		} catch (SecurityException e) {
			log.error("Can not access capability " + capabilityClass.getName(), e);
		}
		
		return m;
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}
}
