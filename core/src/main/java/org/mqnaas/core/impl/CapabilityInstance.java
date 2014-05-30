package org.mqnaas.core.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
 * Represents an instantiated capability class bound to a {@link IResource}.
 * </p>
 * <p>
 * Provides a proxy of the instance to be used when injecting this capability.
 * </p>
 * <p>
 * Provides the {@link IService}s for each {@link ICapability} interface implemented by the represented capability class.
 * </p>
 */
public class CapabilityInstance extends ApplicationInstance {

	private IExecutionService									executionService;

	private Multimap<Class<? extends ICapability>, IService>	services;

	// The resource this capability is bound to
	private IResource											resource;

	private ICapability											proxy;

	// All capability interfaces this capability implements
	private List<Class<? extends ICapability>>					capabilityClasses;

	public CapabilityInstance(Class<? extends ICapability> clazz) {
		super(clazz);

		services = ArrayListMultimap.create();
	}

	public CapabilityInstance(Class<? extends ICapability> clazz, ICapability instance) {
		this(clazz);

		this.instance = instance;

		if (instance instanceof IExecutionService) {
			executionService = (IExecutionService) instance;
		}
	}

	@Override
	public boolean isResolved() {
		return super.isResolved() && executionService != null;
	}

	/**
	 * Determines and returns all capability interfaces implemented by the represented capability
	 */
	public Collection<Class<? extends ICapability>> getCapabilities() {

		if (capabilityClasses == null) {

			capabilityClasses = new ArrayList<Class<? extends ICapability>>();

			for (Class<?> interfaze : ClassUtils.getAllInterfaces(clazz)) {
				// Ignore the ICapability interface itself
				if (interfaze.equals(ICapability.class))
					continue;

				// Ignore all interfaces that do not extend ICapability
				if (!ICapability.class.isAssignableFrom(interfaze))
					continue;

				// Now do the cast: this one is safe because we explicitly checked it before
				@SuppressWarnings("unchecked")
				Class<? extends ICapability> capabilityInterface = (Class<? extends ICapability>) interfaze;
				capabilityClasses.add(capabilityInterface);
			}
		}

		return capabilityClasses;
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

	public void bind(IResource resource) {

		Collection<Class<? extends ICapability>> capabilities = getCapabilities();

		Map<Method, IInternalService> proxyServices = new HashMap<Method, IInternalService>();

		// 1. Create the services of the interfaces (backed by the instance)
		for (Class<? extends ICapability> interfaze : capabilities) {
			for (Method method : interfaze.getMethods()) {
				IInternalService service = new Service(resource, new ServiceMetaData(method, (ICapability) getInstance()));

				// Add the service to the proxy implementation to be able to do the relay
				proxyServices.put(method, service);

				services.put(interfaze, service);
			}
		}

		// 2. Create a proxy for all the interfaces implemented by this capability to redirect all calls to the interfaces to the ExecutionService
		// we use the ClassLoader of getInstance() because it is the only one that has for sure access to all (implemented) interfaces.
		proxy = (ICapability) Proxy.newProxyInstance(getInstance().getClass().getClassLoader(),
				capabilities.toArray(new Class[capabilities.size()]), new ExecutionRelayingInvocationHandler(proxyServices));

		this.resource = resource;
	}

	public void unbind() {

		// 1. Clear the services of the interfaces
		services.clear();

		// 2. Clear proxy
		proxy = null;

		// 3. Clear the instance
		instance = null;

		this.resource = null;
	}

	public Multimap<Class<? extends ICapability>, IService> getServices() {
		return ArrayListMultimap.create(services);
	}

	public IResource getResource() {
		return resource;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Capability ").append(clazz.getSimpleName());
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

	@Override
	public ICapability getProxy() {
		return proxy;
	}

	/*
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
				if (service.getMetadata().getName().equals("execute") && service.getMetadata().getCapabilityClass().equals(ExecutionService.class)) {
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
