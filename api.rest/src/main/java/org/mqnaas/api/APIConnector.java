package org.mqnaas.api;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.annotations.DependingOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple REST API publication test class. To be deleted.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
public class APIConnector implements IApplication {

	private static final Logger	log	= LoggerFactory.getLogger(APIConnector.class);

	@DependingOn
	IRootResourceManagement		rootResourceManagement;

	@DependingOn
	IExecutionService			executionService;

	@DependingOn
	IRESTAPIProvider			restApiProvider;

	@DependingOn
	IServiceProvider			serviceProvider;

	@Override
	public void activate() {

		log.info("Start API test");

		try {
			restApiProvider.publish(rootResourceManagement, IRootResourceManagement.class, "/mqnaas/rootResources");
			restApiProvider.publish(serviceProvider, IServiceProvider.class, "/mqnaas/services");
			// restApiProvider.publish(executionService, IExecutionService.class, "/mqnaas/");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void deactivate() {
	}

}
