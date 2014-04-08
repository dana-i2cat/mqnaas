package org.opennaas.core.impl;

import org.opennaas.core.api.IApplication;

public class ApplicationInstance extends AbstractInstance<IApplication> {

	public ApplicationInstance(Class<? extends IApplication> clazz) {
		super(clazz);
	}

	@Override
	public String toString() {
		return "Application " + clazz.getSimpleName();
	}

}
