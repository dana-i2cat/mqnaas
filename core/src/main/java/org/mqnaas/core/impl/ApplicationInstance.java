package org.mqnaas.core.impl;

import org.mqnaas.core.api.IApplication;

public class ApplicationInstance extends AbstractInstance<IApplication> {

	public ApplicationInstance(Class<? extends IApplication> clazz) {
		super(clazz);
	}

	@Override
	public String toString() {
		return "Application " + clazz.getSimpleName();
	}

}
