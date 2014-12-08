package org.mqnaas.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IObservationFilter;
import org.mqnaas.core.api.IObservationService;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.Specification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionService implements IExecutionService, IObservationService {

	private static final Logger					log	= LoggerFactory.getLogger(ExecutionService.class);

	// Holds the registered notification filters
	private Map<IObservationFilter, IService>	observationFilters;

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType() == Specification.Type.CORE;
	}

	public ExecutionService() {
		observationFilters = new HashMap<IObservationFilter, IService>();
	}

	@Override
	public Object execute(IService service, Object[] parameters) throws InvocationTargetException {

		// Permission?

		// Lock on resource?

		// Transaction active?

		Object result = ((IInternalService) service).execute(parameters);

		// Observations?

		for (IObservationFilter filter : observationFilters.keySet()) {
			if (filter.observes(service, parameters)) {
				Object[] args = filter.getParameters(service, parameters, result);

				boolean asynchronously = false;

				IService notifiedService = observationFilters.get(filter);

				log.debug(getClass().getSimpleName() + ": Observing filter " + filter + " MATCHES, executing '" + notifiedService + "' " + (asynchronously ? "asynchronously" : "synchronously"));

				if (asynchronously) {
					new Thread(new RunnableService(notifiedService, args)).start();
				} else {
					execute(notifiedService, args);
				}
			}
		}

		return result;
	}

	@Override
	public void registerObservation(IObservationFilter filter, IService service) {
		if (filter == null)
			throw new IllegalArgumentException("Filter must be given.");
		if (service == null)
			throw new IllegalArgumentException("Service must be given.");

		observationFilters.put(filter, service);
	}

	private class RunnableService implements Runnable {

		private IService	service;
		private Object[]	parameters;

		public RunnableService(IService service, Object[] parameters) {
			this.service = service;
			this.parameters = parameters;
		}

		@Override
		public void run() {
			try {
				execute(service, parameters);
			} catch (InvocationTargetException e) {
				log.error("Error executing service  " + service.getMetadata().getName() + " asynchronously.", e);
			}
		}

	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

}
