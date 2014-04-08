package org.opennaas.core.impl.notificationfilter;

import org.opennaas.core.api.IObservationFilter;
import org.opennaas.core.api.IService;

public class ServiceFilter implements IObservationFilter {

	private IService observedService;

	public ServiceFilter(IService service) {
		observedService = service;
	}
	
	@Override
	public boolean observes(IService service, Object[] parameters) {
		return service.equals(observedService);
	}

	@Override
	public Object[] getParameters(IService service, Object[] parameters, Object result) {
		return null;
	}
	
	@Override
	public String toString() {
		return "executing service == " + observedService;
	}

}
