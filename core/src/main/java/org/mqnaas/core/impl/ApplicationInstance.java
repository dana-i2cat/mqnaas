package org.mqnaas.core.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.ApplicationInstanceLifeCycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

//TODO Redo the security and instantiation aspects

/**
 * It manages an application instance's
 * <ul>
 * <li><b>class</b>: to represent it as long as no instance exists)</li>
 * <li><b>instance</b>: which could be given when initializing</li>
 * <li><b>two types of dependencies</b>
 * <ol>
 * <li><i>pending</i>: the unresolved application dependencies</li>
 * <li><i>resolved</i>: already resolved application dependencies</li>
 * </ol>
 * </ul>
 * 
 * Dependencies are identified using {@link DependingOn} field annotation.
 * 
 * <p>
 * Provides a proxy of the instance to be used when injecting this application (through {@link ApplicationProxyHolder})
 * </p>
 * <p>
 * Provides the {@link IService}s for each {@link IApplication} interface implemented by the represented application class (through
 * {@link ApplicationProxyHolder}).
 * </p>
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Isart Canyameres Gimenez (i2cat)
 */
public class ApplicationInstance {

	private final Logger								log	= LoggerFactory.getLogger(getClass());

	protected Class<? extends IApplication>				clazz;

	private IApplication								instance;

	private Map<Class<? extends IApplication>, Field>	pendingDependencies;

	private Map<Class<? extends IApplication>, Field>	resolvedDependencies;

	protected Collection<ApplicationInstance>			injectedDependencies;

	private IExecutionService							executionService;

	private ApplicationProxyHolder						proxyHolder;

	private ApplicationInstanceLifeCycleState			state;

	public ApplicationInstance(Class<? extends IApplication> clazz) {
		this(clazz, null);
	}

	public ApplicationInstance(Class<? extends IApplication> clazz, IApplication instance) {

		this.clazz = clazz;

		pendingDependencies = getDependencies(clazz);

		resolvedDependencies = new HashMap<Class<? extends IApplication>, Field>();

		injectedDependencies = new ArrayList<ApplicationInstance>();

		this.instance = instance;

		setState(ApplicationInstanceLifeCycleState.INSTANTIATED);

		proxyHolder = new ApplicationProxyHolder(clazz, getInstance());
	}

	public Class<? extends IApplication> getClazz() {
		return clazz;
	}

	public IApplication getInstance() {
		if (instance == null) {
			try {
				instance = clazz.newInstance();
			} catch (InstantiationException e) {
				// ignore for now
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// ignore for now
				e.printStackTrace();
			}
		}

		return instance;
	}

	/**
	 * @return the injectedDependencies. Returned collection is not the live one, but a copy.
	 */
	public Collection<ApplicationInstance> getInjectedDependencies() {
		return new ArrayList<ApplicationInstance>(injectedDependencies);
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

	/**
	 * Returns the currently pending dependencies, e.g. the missing capability classes.
	 */
	public Collection<Class<? extends IApplication>> getPendingClasses() {
		Collection<Class<? extends IApplication>> pendingClasses = new HashSet<Class<? extends IApplication>>(pendingDependencies.keySet());
		if (executionService == null)
			pendingClasses.add(IExecutionService.class);
		return pendingClasses;
	}

	/**
	 * Returns the currently resolved dependencies, e.g. the already initialized capability classes.
	 */
	public Collection<Class<? extends IApplication>> getResolvedClasses() {
		Collection<Class<? extends IApplication>> resolvedClasses = new HashSet<Class<? extends IApplication>>(resolvedDependencies.keySet());
		if (executionService != null)
			resolvedClasses.add(IExecutionService.class);
		return resolvedClasses;
	}

	public boolean isResolved() {
		return pendingDependencies.isEmpty() && executionService != null;
	}

	/**
	 * Resolves all dependencies that can be satisfied by the given {@link ApplicationInstance}.
	 * 
	 * @param potentialDependency
	 * @return whether the internal state of this instance has changed after this call or not (after the call is using potentialDependency and was not
	 *         before)
	 */
	public <D extends IApplication> boolean resolve(ApplicationInstance dependency) {
		boolean affected = resolveDependencies(dependency);

		boolean execServiceAffected = false;
		if (executionService == null) {
			if (dependency.getApplications().contains(IExecutionService.class)) {
				executionService = (IExecutionService) dependency.getInstance();
				injectedDependencies.add(dependency);
				execServiceAffected = true;
				proxyHolder.setExecutionService(executionService);
			}
		}
		return affected || execServiceAffected;
	}

	/**
	 * Unresolves all dependencies that are being resolved with the given {@link CapabilityInstance}.
	 * 
	 * @param potentialDependency
	 * @return whether the internal state of this instance has changed after this call or not (was using given potential dependency and after the call
	 *         is no longer)
	 */
	public <D extends IApplication> boolean unresolve(ApplicationInstance dependency) {
		boolean affected = unresolveDependencies(dependency);

		boolean execServiceAffected = false;
		if (dependency.getApplications().contains(IExecutionService.class)) {
			if (executionService == dependency.getInstance()) {
				executionService = null;
				injectedDependencies.remove(dependency);
				proxyHolder.setExecutionService(null);
				execServiceAffected = true;
			}
		}

		return affected || execServiceAffected;
	}

	/**
	 * Unresolves all currently resolved dependencies
	 * 
	 */
	public void unresolveAllDependencies() {
		// Iterator-safe implementation for the following:
		// for (ApplicationInstance app: injectedDependencies) unresolve(app);
		// Due to unresolve producing changes in the collections that backs up the foreach iterator, commented code is not safe
		Collection<ApplicationInstance> dependencies = new HashSet<ApplicationInstance>(injectedDependencies);
		for (ApplicationInstance app : dependencies) {
			unresolve(app);
		}
	}

	// public void initServices() {
	// initInstanceServicesAndProxy(null);
	// }

	public void stopServices() {
		clearInstanceServicesAndProxy();
	}

	public Multimap<Class<? extends IApplication>, IService> getServices() {
		Multimap<Class<? extends IApplication>, IInternalService> internalServices = proxyHolder.getServices();
		Multimap<Class<? extends IApplication>, IService> services = ArrayListMultimap.create(internalServices.size(), 3);
		services.putAll(internalServices);
		return services;
	}

	/**
	 * Returns all application interfaces implemented by the represented application
	 * 
	 */
	public Collection<Class<? extends IApplication>> getApplications() {
		Set<Class<? extends IApplication>> appsCopy = new HashSet<Class<? extends IApplication>>();
		appsCopy.addAll(getServices().keySet());
		return appsCopy;
	}

	public IApplication getProxy() {
		return proxyHolder.getProxy();
	}

	/**
	 * Sets the given {@link IResource} as the resource used when executing the services.
	 * 
	 * @param resource
	 *            The resources used when executing services.
	 */
	protected void setResource(IResource resource) {
		proxyHolder.setResource(resource);
	}

	protected void clearInstanceServicesAndProxy() {

		// 1. Clear proxy
		proxyHolder = null;

		// 2. Clear the instance
		instance = null;
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

	/**
	 * Resolves all dependencies that can be satisfied by the given {@link ApplicationInstance}.
	 * 
	 * @param potentialDependency
	 * @return whether the internal state of this instance has changed after this call or not (after the call is using potentialDependency and was not
	 *         before)
	 */
	private <D extends IApplication> boolean resolveDependencies(ApplicationInstance potentialDependency) {

		boolean affected = false;
		for (Class<? extends IApplication> capabilityClass : potentialDependency.getApplications()) {

			if (pendingDependencies.containsKey(capabilityClass)) {
				Field field = pendingDependencies.get(capabilityClass);

				try {
					// Initialize the field of the application or capability
					// TODO Security implications?
					log.debug("Resolving dependency of field {}.{} with {}", clazz.getSimpleName(), field.getName(), capabilityClass);

					field.setAccessible(true);
					field.set(getInstance(), potentialDependency.getProxy());
					resolve(capabilityClass);
					injectedDependencies.add(potentialDependency);
					affected = true;
				} catch (IllegalArgumentException e) {
					// ignore for now
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// ignore for now
					e.printStackTrace();
				}
			}
		}
		return affected;
	}

	/**
	 * Unresolves all dependencies that are being resolved with the given {@link CapabilityInstance}.
	 * 
	 * @param potentialDependency
	 * @return whether the internal state of this instance has changed after this call or not (was using given potential dependency and after the call
	 *         is no longer)
	 */
	private <D extends IApplication> boolean unresolveDependencies(ApplicationInstance potentialDependency) {

		boolean affected = false;
		for (Class<? extends IApplication> capabilityClass : potentialDependency.getApplications()) {

			if (resolvedDependencies.containsKey(capabilityClass)) {
				Field field = resolvedDependencies.get(capabilityClass);

				try {
					// TODO Security implications?
					field.setAccessible(true);
					if (field.get(getInstance()) == potentialDependency.getProxy()) {
						// dependency is being resolved with potentialDependency
						log.debug("Unresolving dependency of field {}.{}", clazz.getSimpleName(), field.getName());

						field.set(getInstance(), null);
						unresolve(capabilityClass);
						injectedDependencies.remove(potentialDependency);
						affected = true;
					}
				} catch (IllegalArgumentException e) {
					// ignore for now
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// ignore for now
					e.printStackTrace();
				}
			}
		}
		return affected;
	}

	/**
	 * Updates the internal dependency state.
	 */
	private void resolve(Class<? extends IApplication> capabilityClass) {
		Field field = pendingDependencies.remove(capabilityClass);
		resolvedDependencies.put(capabilityClass, field);
	}

	/**
	 * Updates the internal dependency state.
	 */
	private void unresolve(Class<? extends IApplication> capabilityClass) {
		Field field = resolvedDependencies.remove(capabilityClass);
		pendingDependencies.put(capabilityClass, field);
	}

	/**
	 * Collects and returns all fields in the given class, which have the @DependingOn annotation.
	 */
	private Map<Class<? extends IApplication>, Field> getDependencies(Class<? extends IApplication> clazz) {
		Map<Class<? extends IApplication>, Field> dependencies = new HashMap<Class<? extends IApplication>, Field>();

		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(DependingOn.class)) {

				if (!(IApplication.class.isAssignableFrom(field.getType()))) {
					throw new IllegalArgumentException(
							"In " + clazz.getName() + " " + field.getType().getName() + " does not implement " + IApplication.class.getName() +
									" and can therefore not be used as a dependency.");
				}

				// The following cast is safe, because it was explicitly checked above
				@SuppressWarnings("unchecked")
				Class<? extends IApplication> type = (Class<? extends IApplication>) field.getType();

				dependencies.put(type, field);
			}
		}

		return dependencies;
	}

}
