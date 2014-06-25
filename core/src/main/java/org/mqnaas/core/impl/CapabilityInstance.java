package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.ClassUtils;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;

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
 */
public class CapabilityInstance extends ApplicationInstance {

	// The resource this capability is bound to
	private IResource									resource;

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
		this.resource = resource;
	}

	public void unbind() {
		this.resource = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mqnaas.core.impl.ApplicationInstance#initServices()
	 */
	@Override
	public void initServices() {
		if (resource == null)
			throw new IllegalStateException("Resource must be set");

		initInstanceServicesAndProxy(resource);
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
		return resource;
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

}
