package org.mqnaas.bundletree;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	static BundleContext	context;

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	public void stop(BundleContext context) throws Exception {

	}

	public static BundleContext getContext() {
		return context;
	}

}
