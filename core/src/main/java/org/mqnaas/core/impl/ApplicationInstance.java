package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.mqnaas.core.api.IApplication;

public class ApplicationInstance extends AbstractInstance<IApplication> {

	// All application interfaces represented application implements
	private List<Class<? extends IApplication>>	applicationClasses;

	public ApplicationInstance(Class<? extends IApplication> clazz) {
		super(clazz);
	}

	@Override
	public String toString() {
		return "Application " + clazz.getSimpleName();
	}

	/**
	 * Determines and returns all application interfaces implemented by the represented application
	 */
	public Collection<Class<? extends IApplication>> getApplications() {

		if (applicationClasses == null) {

			applicationClasses = new ArrayList<Class<? extends IApplication>>();

			for (Class<?> interfaze : ClassUtils.getAllInterfaces(clazz)) {
				// Ignore the IApplication interface itself
				if (interfaze.equals(IApplication.class))
					continue;

				// Ignore all interfaces that do not extend IApplication
				if (!IApplication.class.isAssignableFrom(interfaze))
					continue;

				// Now do the cast: this one is safe because we explicitly checked it before
				@SuppressWarnings("unchecked")
				Class<? extends IApplication> applicationInterface = (Class<? extends IApplication>) interfaze;
				applicationClasses.add(applicationInterface);
			}
		}

		return applicationClasses;
	}

	public IApplication getProxy() {
		// TODO Auto-generated method stub
		// TODO Implement when services are in ApplicationInstances
		return null;
	}

}
