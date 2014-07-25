package org.mqnaas.core.impl;

import java.lang.reflect.InvocationHandler;
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
 * <p>
 * Represents an instantiated application class
 * </p>
 * <p>
 * Provides a proxy of the instance to be used when injecting this application.
 * </p>
 * <p>
 * Provides the {@link IService}s for each {@link IApplication} interface implemented by the represented application class.
 * </p>
 */
public class ApplicationInstance extends AbstractInstance<IApplication> {

	private IExecutionService											executionService;

	// Holds the services available from this application instance, ordered by the interfaces they belong to
	private Multimap<Class<? extends IApplication>, IInternalService>	internalServices;

	private IApplication												proxy;

	// All application interfaces implemented by the represented application
	// TODO this information is redundant, it is also available in the services multimap.
	private Collection<Class<? extends IApplication>>					applicationClasses;

	// InvocationHandler used by the proxy to execute the services offered by this application instance
	private ExecutionRelayingInvocationHandler							invocationHandler;

	public ApplicationInstance(Class<? extends IApplication> clazz) {
		this(clazz, null);
	}

	public ApplicationInstance(Class<? extends IApplication> clazz, IApplication instance) {
		super(clazz);

		this.instance = instance;

		internalServices = ArrayListMultimap.create();

		Collection<Class<? extends IApplication>> appClasses = getApplications();
		// an application without interfaces is not able to offer services
		// applications are forced to implement interfaces extending IApplication in order to publish services
		if (appClasses.isEmpty())
			return;

		// 1. Create the services of the interfaces (backed by the instance)
		for (Class<? extends IApplication> interfaze : appClasses) {
			for (Method method : interfaze.getMethods()) {
				internalServices.put(interfaze, new Service(method, (IApplication) getInstance()));
			}
		}

		// 2. Create the InvocationHandler used by the proxy
		invocationHandler = new ExecutionRelayingInvocationHandler(internalServices.values());

		// 3. Create a proxy for all the interfaces implemented by this capability to redirect all calls to the interfaces to the ExecutionService
		// we use the ClassLoader of getInstance() because it is the only one that has for sure access to all (implemented) interfaces.
		proxy = (IApplication) Proxy.newProxyInstance(getInstance().getClass().getClassLoader(),
				appClasses.toArray(new Class[appClasses.size()]), invocationHandler);

		if (getInstance() instanceof IExecutionService) {
			executionService = (IExecutionService) getInstance();
		}
	}

	@Override
	public boolean isResolved() {
		return super.isResolved() && executionService != null;
	}

	@Override
	public <D extends IApplication> boolean resolve(ApplicationInstance dependency) {
		boolean affected = super.resolve(dependency);

		boolean execServiceAffected = false;
		if (dependency.getApplications().contains(IExecutionService.class)) {
			executionService = (IExecutionService) dependency.getInstance();
			execServiceAffected = true;
		}

		return affected || execServiceAffected;
	}

	@Override
	public <D extends IApplication> boolean unresolve(ApplicationInstance dependency) {
		boolean affected = super.unresolve(dependency);

		boolean execServiceAffected = false;
		if (dependency.getApplications().contains(IExecutionService.class)) {
			if (executionService == dependency.getInstance()) {
				executionService = null;
				execServiceAffected = true;
			}
		}

		return affected || execServiceAffected;
	}

	// public void initServices() {
	// initInstanceServicesAndProxy(null);
	// }

	public void stopServices() {
		clearInstanceServicesAndProxy();
	}

	public Multimap<Class<? extends IApplication>, IService> getServices() {
		Multimap<Class<? extends IApplication>, IService> services = ArrayListMultimap.create(internalServices.size(), 3);
		services.putAll(internalServices);
		return services;
	}

	/**
	 * Determines and returns all application interfaces implemented by the represented application
	 */
	public Collection<Class<? extends IApplication>> getApplications() {
		if (applicationClasses == null) {
			applicationClasses = computeApplications(clazz);
		}
		return applicationClasses;
	}

	public IApplication getProxy() {
		return proxy;
	}

	/**
	 * Sets the given {@link Resource} as the resource used when executing the services.
	 * 
	 * @param resource
	 *            The resources used when executing services.
	 */
	protected void setResource(IResource resource) {
		invocationHandler.setResource(resource);
	}

	protected void clearInstanceServicesAndProxy() {
		// 1. Clear the services of the interfaces
		internalServices.clear();

		// 2. Clear proxy
		proxy = null;

		// 3. Clear the instance
		instance = null;
	}

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
		 * Sets the given {@link Resource} to all managed relayed {@link IService}s.
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

			if (service == null) {
				// A method was called for which no relay exists, e.g.
				// toString(), it will be invoked directly
				result = method.invoke(getInstance(), args);
			} else {
				if (service.getMetadata().getName().equals("execute") && service.getMetadata().getApplicationClass().equals(ExecutionService.class)) {
					// This avoid looping infinitely through proxy calls... TODO add more details
					result = service.execute(args);
				} else {
					result = executionService.execute(service, args);
				}
			}

			return result;
		}
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Application ").append(clazz.getSimpleName());
		sb.append(" [pending=(");
		int i = 0;
		for (Class<? extends IApplication> clazz : getPendingClasses()) {
			if (i > 0)
				sb.append(", ");
			sb.append(clazz.getSimpleName());
			i++;
		}

		sb.append("), resolved=(");
		i = 0;
		for (Class<? extends IApplication> clazz : getResolvedClasses()) {
			if (i > 0)
				sb.append(", ");
			sb.append(clazz.getSimpleName());
			i++;
		}

		sb.append(")]");

		return sb.toString();
	}

}
