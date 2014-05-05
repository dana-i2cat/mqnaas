package org.mqnaas.examples.testapp;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IResourceManagement;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.impl.MQNaaS;
import org.mqnaas.core.impl.notificationfilter.ServiceFilter;
import org.mqnaas.examples.junosrouter.JunosRouter;
import org.mqnaas.examples.openerRouter.OpenerRouter;

public class MyTestApplication implements IApplication {

	@DependingOn
	private IResourceManagement	resourceManagement;

	@DependingOn
	private IServiceProvider	serviceProvider;

	@DependingOn
	private IExecutionService	executionService;

	@Override
	public void onDependenciesResolved() {

		MQNaaS mqNaaS = resourceManagement.getResource(MQNaaS.class);

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

		executionService.registerObservation(new ServiceFilter(observedService), notifiedService);

		resourceManagement.addResource(new JunosRouter());

		resourceManagement.addResource(new OpenerRouter());

		resourceManagement.addResource(new JunosRouter());

		resourceManagement.addResource(new OpenerRouter());

		resourceManagement.addResource(new JunosRouter());

	}

}