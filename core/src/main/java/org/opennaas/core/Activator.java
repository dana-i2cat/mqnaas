package org.opennaas.core;

import org.opennaas.core.impl.BindingManagement;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		new BindingManagement();
	}

	public void stop(BundleContext context) throws Exception {

	}

}
