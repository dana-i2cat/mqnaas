package org.mqnaas.api.mapping;

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
