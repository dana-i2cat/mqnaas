package org.mqnaas.core.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.dependencies.ApplicationInstanceLifeCycleState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
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

	private final Logger						log	= LoggerFactory.getLogger(getClass());

	protected Class<? extends IApplication>		clazz;

	private IApplication						instance;

	private Collection<Dependency>				dependencies;

	// this field is used through reflection (take caution when refactoring)
	private IExecutionService					executionService;

	private ApplicationProxyHolder				proxyHolder;

	private ApplicationInstanceLifeCycleState	state;

	public ApplicationInstance(Class<? extends IApplication> clazz) {
		this(clazz, null);
	}

	public ApplicationInstance(Class<? extends IApplication> clazz, IApplication instance) {

		this.clazz = clazz;

		this.instance = instance;

		dependencies = computeDependencies(clazz, getInstance());

		proxyHolder = new ApplicationProxyHolder(clazz, getInstance());

		setState(ApplicationInstanceLifeCycleState.INSTANTIATED);
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
		Collection<ApplicationInstance> injected = new ArrayList<ApplicationInstance>();
		for (Dependency resolved : getResolvedDependencies()) {
			injected.add(resolved.getResolvedWith());
		}
		return injected;
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
		Collection<Class<? extends IApplication>> pendingClasses = new HashSet<Class<? extends IApplication>>();
		for (Dependency dep : getPendingDependencies()) {
			pendingClasses.add(dep.getFieldType());
		}
		return pendingClasses;
	}

	/**
	 * Returns the currently resolved dependencies, e.g. the already initialized capability classes.
	 */
	public Collection<Class<? extends IApplication>> getResolvedClasses() {
		Collection<Class<? extends IApplication>> resolvedClasses = new HashSet<Class<? extends IApplication>>();
		for (Dependency dep : getResolvedDependencies()) {
			resolvedClasses.add(dep.getFieldType());
		}
		return resolvedClasses;
	}

	public boolean isResolved() {
		for (Dependency dep : getDependencies()) {
			if (!dep.isResolved())
				return false;
		}
		return true;
	}

	/**
	 * Resolves all dependencies that can be satisfied by the given {@link ApplicationInstance}.
	 * 
	 * @param potentialDependency
	 * @return whether the internal state of this instance has changed after this call or not (after the call is using potentialDependency and was not
	 *         before)
	 */
	public boolean resolve(ApplicationInstance dependency) {
		Collection<Dependency> affected = resolveDependencies(dependency);

		Dependency execServiceDep = getExecutionServiceDependency(affected);
		if (execServiceDep != null) {
			// execution service has been resolved
			proxyHolder.setExecutionService(executionService);
		}

		return !affected.isEmpty();
	}

	/**
	 * Unresolves all dependencies that are being resolved with the given {@link CapabilityInstance}.
	 * 
	 * @param potentialDependency
	 * @return whether the internal state of this instance has changed after this call or not (was using given potential dependency and after the call
	 *         is no longer)
	 */
	public boolean unresolve(ApplicationInstance dependency) {
		Collection<Dependency> affected = unresolveDependencies(dependency);

		Dependency execServiceDep = getExecutionServiceDependency(affected);
		if (execServiceDep != null) {
			// execution service has been unresolved
			proxyHolder.setExecutionService(null);
		}
		return !affected.isEmpty();
	}

	/**
	 * Unresolves all currently resolved dependencies
	 * 
	 */
	public void unresolveAllDependencies() {
		// Iterator-safe implementation for the following:
		// for (ApplicationInstance app: getInjectedDependencies()) unresolve(app);
		// Due to unresolve producing changes in the collections that backs up the foreach iterator, commented code is not safe
		// Copying injectedDependencies in another collection and iterate over that one solves the issue
		Collection<ApplicationInstance> injected = new HashSet<ApplicationInstance>(getInjectedDependencies());
		for (ApplicationInstance app : injected) {
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

	private Collection<Dependency> getDependencies() {
		return dependencies;
	}

	/**
	 * Resolves all dependencies that can be satisfied by the given {@link ApplicationInstance}.
	 * 
	 * @param potentialDependency
	 * @return whether the internal state of this instance has changed after this call or not (after the call is using potentialDependency and was not
	 *         before)
	 */
	private Collection<Dependency> resolveDependencies(ApplicationInstance potentialDependency) {
		Collection<Dependency> affected = new ArrayList<Dependency>();

		for (Class<? extends IApplication> capabilityClass : potentialDependency.getApplications()) {

			for (Dependency dep : getPendingDependencies()) {
				// FIXME shouldn't it be is assignable from?
				if (dep.getFieldType().equals(capabilityClass)) {

					try {
						// Initialize the field of the application or capability
						// TODO Security implications?
						log.debug("Resolving dependency of field {}.{} with {}", clazz.getSimpleName(), dep.getField().getName(), capabilityClass);

						dep.getField().setAccessible(true);
						dep.getField().set(dep.getInstance(), potentialDependency.getProxy());
						dep.setResolvedWith(potentialDependency);
						affected.add(dep);
					} catch (IllegalArgumentException e) {
						// ignore for now
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// ignore for now
						e.printStackTrace();
					}

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
	private Collection<Dependency> unresolveDependencies(ApplicationInstance potentialDependency) {
		Collection<Dependency> affected = new ArrayList<Dependency>();

		for (Dependency dep : getResolvedDependencies()) {
			if (dep.getResolvedWith() == potentialDependency) {
				// dependency is being resolved with potentialDependency
				try {
					log.debug("Unresolving dependency of field {}.{}", clazz.getSimpleName(), dep.getField().getName());
					// TODO Security implications?
					dep.getField().setAccessible(true);
					dep.getField().set(dep.getInstance(), null);
					dep.setResolvedWith(null);
					affected.add(dep);
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

	private Collection<Dependency> computeDependencies(Class<? extends IApplication> clazz, IApplication instance) {

		// Compute dependencies based on clazz
		Collection<Dependency> dependencies = computeApplicationDependencies(clazz);
		// set instance
		for (Dependency dependency : dependencies) {
			dependency.setInstance(instance);
		}

		// add executionService dependency
		try {
			dependencies.add(new Dependency(ApplicationInstance.class.getDeclaredField("executionService"), IExecutionService.class, this));
		} catch (SecurityException e) {
			// This exception should never happen (a class should has access to its declared private fields)
			log.error("Error populating application dependencies. Unable to define execution service dependency: ", e);
		} catch (NoSuchFieldException e) {
			// This exception should never happen (unless a programmer removes/renames executionService field)
			log.error("Error populating application dependencies. Unable to define execution service dependency: ", e);
		}

		return dependencies;
	}

	/**
	 * Collects and returns all fields in the given class, which have the @DependingOn annotation.
	 */
	private Collection<Dependency> computeApplicationDependencies(Class<? extends IApplication> clazz) {

		Collection<Dependency> dependencies = new ArrayList<Dependency>();

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

				dependencies.add(new Dependency(field, type));
			}
		}

		return dependencies;
	}

	private Iterable<Dependency> getResolvedDependencies() {
		Predicate<Dependency> isResolved = new Predicate<Dependency>() {
			@Override
			public boolean apply(Dependency dep) {
				return dep.isResolved();
			}
		};
		return Iterables.filter(getDependencies(), isResolved);
	}

	private Iterable<Dependency> getPendingDependencies() {
		Predicate<Dependency> isPending = new Predicate<Dependency>() {
			@Override
			public boolean apply(Dependency dep) {
				return !dep.isResolved();
			}
		};
		return Iterables.filter(getDependencies(), isPending);
	}

	private Dependency getExecutionServiceDependency(Collection<Dependency> dependencies) {
		for (Dependency dep : dependencies) {
			if (dep.getInstance() == this && dep.getFieldType().equals(IExecutionService.class))
				return dep;
		}
		return null;
	}

	/**
	 * Represents a dependency in an instance that is to be resolved by an ApplicationInstance. Dependencies are identified by its declared type and
	 * the field that holds it in the instance.
	 * 
	 * @author Isart Canyameres Gimenez (i2cat)
	 * 
	 */
	private class Dependency {

		// the field identifying this Dependency
		private Field							field;
		// the type of this Dependency
		private Class<? extends IApplication>	fieldType;
		// the instance having this Dependency
		private Object							instance;

		// ApplicationInstance this Dependency is resolvedWith (the field is set with a proxy, not the ApplicationInstance itself)
		private ApplicationInstance				resolvedWith;

		public Dependency(Field field, Class<? extends IApplication> type) {
			this.field = field;
			this.fieldType = type;
		}

		public Dependency(Field field, Class<? extends IApplication> type, Object instance) {
			this(field, type);
			this.instance = instance;
		}

		/**
		 * @return the field
		 */
		public Field getField() {
			return field;
		}

		/**
		 * @return the fieldType
		 */
		public Class<? extends IApplication> getFieldType() {
			return fieldType;
		}

		/**
		 * @return the instance
		 */
		public Object getInstance() {
			return instance;
		}

		/**
		 * @param instance
		 *            the instance to set
		 */
		public void setInstance(IApplication instance) {
			this.instance = instance;
		}

		/**
		 * @return the resolvedWith
		 */
		public ApplicationInstance getResolvedWith() {
			return resolvedWith;
		}

		/**
		 * @param resolvedWith
		 *            the resolvedWith to set
		 */
		public void setResolvedWith(ApplicationInstance resolvedWith) {
			this.resolvedWith = resolvedWith;
		}

		public boolean isResolved() {
			return getResolvedWith() != null;
		}

	}

}
