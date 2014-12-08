package org.mqnaas.core.impl.notificationfilter;

import org.mqnaas.core.api.IService;

public class ServiceFilterWithParams extends ServiceFilter {

	public ServiceFilterWithParams(IService service) {
		super(service);
	}
	
	@Override
	public Object[] getParameters(IService service, Object[] parameters, Object result) {
		return parameters;
	}

}
