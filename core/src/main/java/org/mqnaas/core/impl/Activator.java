package org.mqnaas.core.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		initCore();
	}

	public void stop(BundleContext context) throws Exception {

	}

	private void initCore() throws Exception {

		// The inner core services are instantiated directly...
		// TODO resolve the instances implementing these interfaces using a internal resolving mechanism. they should be resolved before other
		// dependencies resolution
		ExecutionService executionServiceInstance = new ExecutionService();

		BindingManagement bindingManagement = new BindingManagement();
		bindingManagement.resourceManagement = new RootResourceManagement();
		bindingManagement.bindingDecider = new BinderDecider();
		bindingManagement.executionService = executionServiceInstance;
		bindingManagement.observationService = executionServiceInstance;

		bindingManagement.init();

	}

}
