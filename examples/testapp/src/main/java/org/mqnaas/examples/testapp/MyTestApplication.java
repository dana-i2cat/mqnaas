package org.mqnaas.examples.testapp;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IObservationService;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.impl.notificationfilter.ServiceFilter;

public class MyTestApplication implements IApplication {

	@DependingOn
	private IRootResourceManagement	resourceManagement;

	@DependingOn
	private IServiceProvider		serviceProvider;

	@DependingOn
	private IExecutionService		executionService;

	@DependingOn
	private IObservationService		observationService;

	@Override
	public void onDependenciesResolved() {

		IRootResource mqNaaS = resourceManagement.getCore();

		IService observedService = null;
		IService notifiedService = null;
		try {
			observedService = serviceProvider.getService(mqNaaS, "resourceAdded");
			notifiedService = serviceProvider.getService(mqNaaS, "printAvailableServices");
		} catch (ServiceNotFoundException e) {
			// FIXME this should not happen
			e.printStackTrace();
		}

		executionService.execute(notifiedService, null);

		observationService.registerObservation(new ServiceFilter(observedService), notifiedService);

		resourceManagement.createRootResource(RootResourceDescriptor.create(new Specification(Type.ROUTER, "Junos")));

		resourceManagement.createRootResource(RootResourceDescriptor.create(new Specification(Type.ROUTER, "Opener")));

		resourceManagement.createRootResource(RootResourceDescriptor.create(new Specification(Type.ROUTER, "Junos")));

		resourceManagement.createRootResource(RootResourceDescriptor.create(new Specification(Type.ROUTER, "Opener")));

		resourceManagement.createRootResource(RootResourceDescriptor.create(new Specification(Type.ROUTER, "Junos")));

	}

}
