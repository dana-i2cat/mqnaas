package org.mqnaas.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IObservationFilter;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;

public class ExecutionService implements IExecutionService {

	// Holds the registered notification filters
	private Map<IObservationFilter, IService>	observationFilters;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof OpenNaaS;
	}

	public ExecutionService() {
		observationFilters = new HashMap<IObservationFilter, IService>();
	}

	@Override
	public Object execute(IService service, Object[] parameters) {

		// Permission?

		// Lock on resource?

		// Transaction active?

		Object result = service.execute(parameters);

		// Observations?

		for (IObservationFilter filter : observationFilters.keySet()) {
			if (filter.observes(service, parameters)) {
				Object[] args = filter.getParameters(service, parameters, result);

				boolean asynchronously = false;

				IService notifiedService = observationFilters.get(filter);

				System.out
						.println(getClass().getSimpleName() + ": Observing filter " + filter + " MATCHES, executing '" + notifiedService + "' " + (asynchronously ? "asynchronously" : "synchronously"));

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
			execute(service, parameters);
		}

	}

}
