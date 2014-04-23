package org.opennaas.mqnaas.examples.testapp;

import org.opennaas.core.annotation.DependingOn;
import org.opennaas.core.api.IApplication;
import org.opennaas.core.api.IBindingManagement;
import org.opennaas.core.api.IExecutionService;
import org.opennaas.core.api.IResourceManagement;
import org.opennaas.core.api.IService;
import org.opennaas.core.impl.OpenNaaS;
import org.opennaas.core.impl.notificationfilter.ServiceFilter;
import org.opennaas.junosrouter.JunosRouter;
import org.opennaas.openerRouter.OpenerRouter;

public class MyTestApplication implements IApplication {

	@DependingOn
	private IResourceManagement resourceManagement;

	@DependingOn
	private IBindingManagement bindingManagement;
	
	@DependingOn
	private IExecutionService executionService;

	@Override
	public void onDependenciesResolved() {
		
		OpenNaaS openNaaS = resourceManagement.getResource(OpenNaaS.class);
		
		IService observedService = bindingManagement.getService(openNaaS, "resourceAdded");
		IService notifiedService = bindingManagement.getService(openNaaS, "printAvailableServices");
		
		notifiedService.execute(null);

		executionService.registerObservation(new ServiceFilter(observedService), notifiedService);
		
		resourceManagement.addResource(new JunosRouter());
		
		resourceManagement.addResource(new OpenerRouter());
		
		resourceManagement.addResource(new JunosRouter());

		resourceManagement.addResource(new OpenerRouter());

		resourceManagement.addResource(new JunosRouter());

	}

}