package org.mqnaas.api.mapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of {@link IMethodMapper}s to support mapping from methods from a Web API interface to a Java interface.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * 
 */
public class APIMapper {

	private Class<?>					interfaceAPI;

	// private ResourceProvider resourceProvider;

	private Map<Method, MethodMapper>	methodMappers	= new HashMap<Method, MethodMapper>();

	public APIMapper(Class<?> interfaceAPI, Class<?> interfaceJava) {
		this.interfaceAPI = interfaceAPI;
		// this.resourceProvider = new CapabilityResourceProvider(instanceJava);
	}

	public Class<?> getInterfaceAPI() {
		return interfaceAPI;
	}

	public void addMethodMapper(Method apiMethod, MethodMapper mm) {
		methodMappers.put(apiMethod, mm);
	}

	public MethodMapper getMethodMapper(Method m) {
		return methodMappers.get(m);
	}

}
