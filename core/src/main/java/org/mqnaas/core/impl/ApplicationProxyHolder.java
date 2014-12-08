package org.mqnaas.core.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Holds a proxy for all the interfaces implemented by given application that redirects all calls to the interfaces to the ExecutionService.
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class ApplicationProxyHolder {

	private IApplication												instance;

	private IExecutionService											executionService;

	// Holds the services available from this application instance, ordered by the interfaces they belong to
	private Multimap<Class<? extends IApplication>, IInternalService>	internalServices;

	private IApplication												proxy;

	// InvocationHandler used by the proxy to execute the services offered by this application instance
	private ExecutionRelayingInvocationHandler							invocationHandler;

	public ApplicationProxyHolder(Class<? extends IApplication> clazz, IApplication instance) {
		if (instance == null)
			throw new IllegalArgumentException("Argument instance must not be null");

		this.instance = instance;

		initProxyRelatedDataStructures(clazz, instance);
	}

	/**
	 * Sets the given {@link IResource} as the resource used when executing the services.
	 * 
	 * @param resource
	 *            The resources used when executing services.
	 */
	public void setResource(IResource resource) {
		invocationHandler.setResource(resource);
	}

	/**
	 * Sets the given {@link IExecutionService} as the execution service to be used to execute the services
	 * 
	 * @param executionService
	 *            The execution service to execute all services.
	 */
	public void setExecutionService(IExecutionService executionService) {
		this.executionService = executionService;
	}

	/**
	 * Returns a proxy for all the interfaces implemented by given application that redirects all calls to the interfaces to the ExecutionService.
	 * 
	 * @return proxy for all the interfaces implemented by given application that redirects all calls to the interfaces to the ExecutionService.
	 */
	public IApplication getProxy() {
		return proxy;
	}

	/**
	 * 
	 * @return services available from this application instance, ordered by the interfaces they belong to
	 */
	public Multimap<Class<? extends IApplication>, IInternalService> getServices() {
		return internalServices;
	}

	private void initProxyRelatedDataStructures(Class<? extends IApplication> clazz, IApplication instance) {

		internalServices = ArrayListMultimap.create();

		Collection<Class<? extends IApplication>> appClasses = computeApplications(clazz);
		// an application without interfaces is not able to offer services
		// applications are forced to implement interfaces extending IApplication in order to publish services
		if (appClasses.isEmpty())
			return;

		// 1. Create the services of the interfaces (backed by the instance)
		for (Class<? extends IApplication> interfaze : appClasses) {
			for (Method method : interfaze.getMethods()) {
				internalServices.put(interfaze, new Service(method, instance, interfaze));
			}
		}

		// 2. Create the InvocationHandler used by the proxy
		invocationHandler = new ExecutionRelayingInvocationHandler(internalServices.values());

		// 3. Create a proxy for all the interfaces implemented by the application to redirect all calls to the interfaces to the ExecutionService
		// we use the ClassLoader of instance because it is the only one that has for sure access to all (implemented) interfaces.
		proxy = (IApplication) Proxy.newProxyInstance(instance.getClass().getClassLoader(),
				appClasses.toArray(new Class[appClasses.size()]), invocationHandler);

		if (instance instanceof IExecutionService) {
			executionService = (IExecutionService) instance;
		}
	}

	/**
	 * Retrieves interfaces extending IApplication which are implemented by given clazz
	 * 
	 * @param clazz
	 * @return interfaces extending IApplication which are implemented by given clazz
	 */
	private static Collection<Class<? extends IApplication>> computeApplications(Class<? extends IApplication> clazz) {

		Collection<Class<? extends IApplication>> applicationClasses = new ArrayList<Class<? extends IApplication>>();
		for (Class<?> interfaze : ClassUtils.getAllInterfaces(clazz)) {
			// Ignore the IApplication interface itself
			if (interfaze.equals(IApplication.class))
				continue;
			// Ignore the ICapability interface itself
			if (interfaze.equals(ICapability.class))
				continue;

			// Ignore all interfaces that do not extend IApplication
			if (!IApplication.class.isAssignableFrom(interfaze))
				continue;

			// Now do the cast: this one is safe because we explicitly checked it before
			@SuppressWarnings("unchecked")
			Class<? extends IApplication> applicationInterface = (Class<? extends IApplication>) interfaze;
			applicationClasses.add(applicationInterface);
		}

		return applicationClasses;
	}

	/**
	 * Relays the call to a capability implementations (an OSGi service) to an MQNaaS Service.
	 * 
	 * @author Georg Mansky-Kummert (i2CAT)
	 * 
	 */
	private class ExecutionRelayingInvocationHandler implements InvocationHandler {

		private Map<Method, IInternalService>	relays;

		public ExecutionRelayingInvocationHandler(Collection<IInternalService> relayedServices) {

			relays = new HashMap<Method, IInternalService>();

			for (IInternalService relayedService : relayedServices) {
				relays.put(relayedService.getMetadata().getMethod(), relayedService);
			}

		}

		/**
		 * Sets the given {@link IResource} to all managed relayed {@link IService}s.
		 * 
		 * @param resource
		 *            The Resource handed to the Services.
		 */
		public void setResource(IResource resource) {

			for (IInternalService relayedService : relays.values()) {
				relayedService.setResource(resource);
			}
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

			IInternalService service = relays.get(method);

			Object result;

			try {
				if (service == null) {
					// A method was called for which no relay exists, e.g.
					// toString(), it will be invoked directly
					result = method.invoke(instance, args);
				} else {
					if (service.getMetadata().getName().equals("execute") && IExecutionService.class.isAssignableFrom(service.getMetadata()
							.getApplicationClass())) {
						// This avoid looping infinitely through proxy calls... TODO add more details
						result = service.execute(args);
					} else {
						result = executionService.execute(service, args);
					}
				}

			} catch (InvocationTargetException e) {
				throw e.getCause();
			}

			return result;
		}
	}

}
