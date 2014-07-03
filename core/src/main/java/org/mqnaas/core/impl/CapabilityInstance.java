package org.mqnaas.core.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.impl.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Represents an instantiated capability class bound to a {@link IResource}.
 * </p>
 * <p>
 * Provides a proxy of the instance to be used when injecting this capability.
 * </p>
 * <p>
 * Provides the {@link IService}s for each {@link ICapability} interface implemented by the represented capability class.
 * </p>
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 */
public class CapabilityInstance extends ApplicationInstance {

	private static final Logger							log	= LoggerFactory.getLogger(CapabilityInstance.class);

	// All capability interfaces this capability implements
	private Collection<Class<? extends ICapability>>	capabilityClasses;

	public CapabilityInstance(Class<? extends ICapability> clazz) {
		super(clazz);
	}

	public CapabilityInstance(Class<? extends ICapability> clazz, ICapability instance) {
		super(clazz, instance);
	}

	/**
	 * Determines and returns all capability interfaces implemented by the represented capability
	 */
	public Collection<Class<? extends ICapability>> getCapabilities() {
		if (capabilityClasses == null) {
			capabilityClasses = computeCapabilities(clazz);
		}
		return capabilityClasses;
	}

	public void bind(IResource resource) {

		// Safe cast checked in constructors
		ICapability capability = (ICapability) getInstance();
		try {
			// Inject resource to capability instance
			injectResourceToCapability(capability, resource);
		} catch (IllegalArgumentException e) {
			// this should not happen, this ICapability has an IResource field to be injected
			log.error("Error injecting resource {} in capability {}.", resource, capability);
			log.error("Exception: ", e);
		} catch (IllegalAccessException e) {
			// this should not happen, this method should be able to inject field value
			log.error("Error injecting resource {} in capability {}.", resource, capability);
			log.error("Exception: ", e);
		}

		setResource(resource);
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

	public void unbind() {
		setResource(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mqnaas.core.impl.ApplicationInstance#stopServices()
	 */
	@Override
	public void stopServices() {
		clearInstanceServicesAndProxy();
	}

	public IResource getResource() {
		return super.getResource();
	}

	protected static Collection<Class<? extends ICapability>> computeCapabilities(Class<? extends IApplication> clazz) {
		Collection<Class<? extends ICapability>> capabilityClasses = new ArrayList<Class<? extends ICapability>>();

		for (Class<?> interfaze : ClassUtils.getAllInterfaces(clazz)) {
			// Ignore the ICapability interface itself
			if (interfaze.equals(ICapability.class))
				continue;

			// Ignore all interfaces that do not extend ICapability
			if (!ICapability.class.isAssignableFrom(interfaze))
				continue;

			// Now do the cast: this one is safe because we explicitly checked it before
			@SuppressWarnings("unchecked")
			Class<? extends ICapability> capabilityInterface = (Class<? extends ICapability>) interfaze;
			capabilityClasses.add(capabilityInterface);
		}

		return capabilityClasses;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Capability ").append(clazz.getSimpleName());
		sb.append(" [pending=(");
		int i = 0;
		for (Class<? extends IApplication> clazz : getPendingClasses()) {
			if (i > 0)
				sb.append(", ");
			sb.append(clazz.getSimpleName());
			i++;
		}

		sb.append("), resolved=(");
		i = 0;
		for (Class<? extends IApplication> clazz : getResolvedClasses()) {
			if (i > 0)
				sb.append(", ");
			sb.append(clazz.getSimpleName());
			i++;
		}

		sb.append(")]");

		return sb.toString();
	}

}
