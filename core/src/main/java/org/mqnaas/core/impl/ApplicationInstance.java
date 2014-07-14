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
import org.mqnaas.core.impl.dependencies.ApplicationInstanceLifeCycleState;

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

	private IExecutionService									executionService;

	private Multimap<Class<? extends IApplication>, IService>	services;

	private IApplication										proxy;

	// All application interfaces represented application implements
	private Collection<Class<? extends IApplication>>			applicationClasses;

	private ApplicationInstanceLifeCycleState					state;

	public ApplicationInstance(Class<? extends IApplication> clazz) {
		super(clazz);

		services = ArrayListMultimap.create();

		setState(ApplicationInstanceLifeCycleState.INSTANTIATED);
	}

	public ApplicationInstance(Class<? extends IApplication> clazz, IApplication instance) {
		this(clazz);

		this.instance = instance;

		if (instance instanceof IExecutionService) {
			executionService = (IExecutionService) instance;
		}
	}

	/**
	 * @return the state
	 */
	public ApplicationInstanceLifeCycleState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(ApplicationInstanceLifeCycleState state) {
		this.state = state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mqnaas.core.impl.AbstractInstance#getPendingClasses()
	 */
	@Override
	public Collection<Class<? extends IApplication>> getPendingClasses() {
		Collection<Class<? extends IApplication>> pendingClasses = super.getPendingClasses();
		if (executionService == null)
			pendingClasses.add(IExecutionService.class);
		return pendingClasses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mqnaas.core.impl.AbstractInstance#getResolvedClasses()
	 */
	@Override
	public Collection<Class<? extends IApplication>> getResolvedClasses() {
		Collection<Class<? extends IApplication>> resolvedClasses = super.getResolvedClasses();
		if (executionService != null)
			resolvedClasses.add(IExecutionService.class);
		return resolvedClasses;
	}

	@Override
	public boolean isResolved() {
		return super.isResolved() && executionService != null;
	}

	@Override
	public <D extends IApplication> boolean resolve(ApplicationInstance dependency) {
		boolean affected = super.resolve(dependency);

		boolean execServiceAffected = false;
		if (executionService == null) {
			if (dependency.getApplications().contains(IExecutionService.class)) {
				executionService = (IExecutionService) dependency.getInstance();
				injectedDependencies.add(dependency);
				execServiceAffected = true;
			}
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
				injectedDependencies.remove(dependency);
				execServiceAffected = true;
			}
		}

		return affected || execServiceAffected;
	}

	public void initServices() {
		initInstanceServicesAndProxy(null);
	}

	public void stopServices() {
		clearInstanceServicesAndProxy();
	}

	public Multimap<Class<? extends IApplication>, IService> getServices() {
		return ArrayListMultimap.create(services);
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

	protected void initInstanceServicesAndProxy(IResource resource) {

		Collection<Class<? extends IApplication>> appClasses = getApplications();
		// an application without interfaces is not able to offer services
		// applications are forced to implement interfaces extending IApplication in order to publish services
		if (appClasses.isEmpty())
			return;

		Map<Method, IInternalService> proxyServices = new HashMap<Method, IInternalService>();

		// 1. Create the services of the interfaces (backed by the instance)
		for (Class<? extends IApplication> interfaze : appClasses) {
			for (Method method : interfaze.getMethods()) {
				IInternalService service = new Service(resource, new ServiceMetaData(method, (IApplication) getInstance()));

				// Add the service to the proxy implementation to be able to do the relay
				proxyServices.put(method, service);

				services.put(interfaze, service);
			}
		}

		// 2. Create a proxy for all the interfaces implemented by this capability to redirect all calls to the interfaces to the ExecutionService
		// we use the ClassLoader of getInstance() because it is the only one that has for sure access to all (implemented) interfaces.
		proxy = (IApplication) Proxy.newProxyInstance(getInstance().getClass().getClassLoader(),
				appClasses.toArray(new Class[appClasses.size()]), new ExecutionRelayingInvocationHandler(proxyServices));

	}

	protected void clearInstanceServicesAndProxy() {
		// 1. Clear the services of the interfaces
		services.clear();

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

		public ExecutionRelayingInvocationHandler(Map<Method, IInternalService> relays) {
			this.relays = relays;
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

}
