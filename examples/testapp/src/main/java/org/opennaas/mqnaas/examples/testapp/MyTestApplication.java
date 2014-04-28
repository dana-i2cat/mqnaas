package org.opennaas.mqnaas.examples.testapp;

import org.opennaas.core.annotation.DependingOn;
import org.opennaas.core.api.IApplication;
import org.opennaas.core.api.IBindingManagement;
import org.opennaas.core.api.IExecutionService;
import org.opennaas.core.api.IResourceManagement;
import org.opennaas.core.api.IService;
import org.opennaas.core.api.Specification;
import org.opennaas.core.api.Specification.Type;
import org.opennaas.core.impl.OpenNaaS;
import org.opennaas.core.impl.notificationfilter.ServiceFilter;

public class MyTestApplication implements IApplication {

	@DependingOn
	private IResourceManagement	resourceManagement;

	@DependingOn
	private IBindingManagement	bindingManagement;

	@DependingOn
	private IExecutionService	executionService;

	@Override
	public void onDependenciesResolved() {

		OpenNaaS openNaaS = resourceManagement.getRootResource(OpenNaaS.class);

		IService observedService = bindingManagement.getService(openNaaS, "resourceAdded");
		IService notifiedService = bindingManagement.getService(openNaaS, "printAvailableServices");

		notifiedService.execute(null);

		executionService.registerObservation(new ServiceFilter(observedService), notifiedService);

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Junos"));

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Opener"));

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Junos"));

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Opener"));

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Junos"));

	}

}