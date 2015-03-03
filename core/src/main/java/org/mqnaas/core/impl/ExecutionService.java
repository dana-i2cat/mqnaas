package org.mqnaas.core.impl;

/*
 * #%L
 * MQNaaS :: Core
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IObservationFilter;
import org.mqnaas.core.api.IObservationService;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
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
	public void activate() throws ApplicationActivationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

}
