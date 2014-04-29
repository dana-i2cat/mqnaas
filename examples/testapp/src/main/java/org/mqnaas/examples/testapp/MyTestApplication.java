package org.mqnaas.examples.testapp;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IBindingManagement;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.notificationfilter.ServiceFilter;

public class MyTestApplication implements IApplication {

	@DependingOn
	private IRootResourceManagement	resourceManagement;

	@DependingOn
	private IBindingManagement		bindingManagement;

	@DependingOn
	private IExecutionService		executionService;

	@Override
	public void onDependenciesResolved() {

		IRootResource mqNaaS = resourceManagement.getRootResource(new Specification(Specification.Type.CORE));

		IService observedService = bindingManagement.getService(mqNaaS, "resourceAdded");
		IService notifiedService = bindingManagement.getService(mqNaaS, "printAvailableServices");

		notifiedService.execute(null);

		executionService.registerObservation(new ServiceFilter(observedService), notifiedService);

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Junos"));

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Opener"));

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Junos"));

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Opener"));

		resourceManagement.createRootResource(new Specification(Type.ROUTER, "Junos"));

	}

}