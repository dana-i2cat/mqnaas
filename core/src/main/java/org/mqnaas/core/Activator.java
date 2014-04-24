package org.mqnaas.core;

import org.mqnaas.core.impl.BindingManagement;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		new BindingManagement();
	}

	public void stop(BundleContext context) throws Exception {

	}

}
