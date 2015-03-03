package org.mqnaas.api.mapping;

/*
 * #%L
 * MQNaaS :: REST API Provider
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.i2cat.utils.StringBuilderUtils;

/**
 * A collection of {@link IMethodMapper}s to support mapping from methods from a Web API interface to a Java interface.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
public class APIMapper implements InvocationHandler {

	private Class<?>					interfaceAPI;

	private Map<Method, MethodMapper>	methodMappers	= new HashMap<Method, MethodMapper>();

	public APIMapper(Class<?> interfaceAPI, Class<?> interfaceJava) {
		this.interfaceAPI = interfaceAPI;
	}

	public Class<?> getInterfaceAPI() {
		return interfaceAPI;
	}

	public void addMethodMapper(Method apiMethod, MethodMapper mm) {
		methodMappers.put(apiMethod, mm);
	}

	@Override
	public Object invoke(Object arg0, Method method, Object[] params) throws Throwable {
		System.out.println("Invoking " + method.getName() + " on " + arg0.getClass() + " with params " + Arrays.toString(params));

		MethodMapper mm = methodMappers.get(method);

		if (mm == null) {
			StringBuilder sb = StringBuilderUtils.create(params);
			sb.insert(0, "No MethodMapper avaiable for ").append(")");

			// TODO Re-think and re-do behavior in case of failure
			throw new RuntimeException(sb.toString());
		}

		return mm.invoke(arg0, params);
	}
}
