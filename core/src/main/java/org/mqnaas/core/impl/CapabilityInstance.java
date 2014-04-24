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
public class CapabilityInstance extends AbstractInstance<ICapability> {

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
	public <D extends ICapability> void resolve(CapabilityInstance dependency) {
		super.resolve(dependency);

		if (dependency.getCapabilities().contains(IExecutionService.class)) {
			executionService = (IExecutionService) dependency.getInstance();
		}
	}

	public void bind(IResource resource) {

		Collection<Class<? extends ICapability>> capabilities = getCapabilities();

		Map<Method, IService> proxyServices = new HashMap<Method, IService>();

		// 1. Create the services of the interfaces (backed by the instance)
		for (Class<? extends ICapability> interfaze : capabilities) {
			for (Method method : interfaze.getMethods()) {
				Service service = new Service(resource, getInstance(), method);

				// Add the service to the proxy implementation to be able to do the relay
				proxyServices.put(method, service);

				services.put(interfaze, service);
			}
		}

		// 2. Create a proxy for all the interfaces implemented by this
		// capability to redirect all calls to the interfaces to the
		// ExecutionService
		proxy = (ICapability) Proxy.newProxyInstance(
				capabilities.iterator().next().getClassLoader(),
				capabilities.toArray(new Class[capabilities.size()]),
				new ExecutionRelayingInvocationHandler(proxyServices));

		this.resource = resource;
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
		for (Class<? extends ICapability> clazz : getPendingClasses()) {
			if (i > 0)
				sb.append(", ");
			sb.append(clazz.getSimpleName());
			i++;
		}

		sb.append("), resolved=(");
		i = 0;
		for (Class<? extends ICapability> clazz : getResolvedClasses()) {
			if (i > 0)
				sb.append(", ");
			sb.append(clazz.getSimpleName());
			i++;
		}

		sb.append(")]");

		return sb.toString();
	}

	public ICapability getProxy() {
		return proxy;
	}

	/*
	 * Relays the call to a capability implementations (an OSGi service) to an OpenNaaS Service.
	 */
	private class ExecutionRelayingInvocationHandler implements InvocationHandler {

		private Map<Method, IService>	relays;

		public ExecutionRelayingInvocationHandler(Map<Method, IService> relays) {
			this.relays = relays;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			IService service = relays.get(method);

			Object result;

			if (service == null) {
				// A method was called for which no relay exists, e.g.
				// toString(), it will be invoked directly
				result = method.invoke(getInstance(), args);
			} else {
				if (service.getName().equals("execute") && service.getCapabilityClass().equals(ExecutionService.class)) {
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
