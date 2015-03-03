package org.mqnaas.core.impl.notificationfilter;

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

import org.mqnaas.core.api.IObservationFilter;
import org.mqnaas.core.api.IService;

public class ServiceFilter implements IObservationFilter {

	private IService	observedService;

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
