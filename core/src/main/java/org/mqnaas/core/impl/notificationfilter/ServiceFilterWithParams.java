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
