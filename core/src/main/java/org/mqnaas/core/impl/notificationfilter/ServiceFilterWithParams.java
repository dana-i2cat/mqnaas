package org.mqnaas.core.impl.notificationfilter;

import java.util.Arrays;

import org.mqnaas.core.api.IService;

public class ServiceFilterWithParams extends ServiceFilter {

	public ServiceFilterWithParams(IService service) {
		super(service);
	}
	
	@Override
	public Object[] getParameters(IService service, Object[] parameters, Object result) {
		
		// We only need the first parameters;
		parameters = Arrays.copyOf(parameters, 1);
		
		return parameters;
	}

}
