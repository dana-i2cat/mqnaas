package org.mqnaas.core.impl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.annotations.DependingOn;

//TODO Redo the security and instantiation aspects

/**
 * <p>
 * Encapsulates the common aspects of application and capability instances.
 * </p>
 * 
 * It manages an instance's
 * <ul>
 * <li><b>class</b>: to represent it as long as no instance exists)</li>
 * <li><b>instance</b>: which could be given when initializing</li>
 * <li><b>two types of dependencies</b>
 * <ol>
 * <li><i>pending</i>: the unresolved capability dependencies</li>
 * <li><i>resolved</i>: already resolved capability dependencies</li>
 * </ol>
 * </ul>
 * 
 */
public abstract class AbstractInstance<T> {

	protected Class<? extends T>						clazz;

	public T											instance;

	private Map<Class<? extends ICapability>, Field>	pendingDependencies;

	private Map<Class<? extends ICapability>, Field>	resolvedDependencies;

	public AbstractInstance(Class<? extends T> clazz) {
		this.clazz = clazz;

		pendingDependencies = getDependencies(clazz);

		resolvedDependencies = new HashMap<Class<? extends ICapability>, Field>();
	}

	protected AbstractInstance(Class<? extends T> clazz, T instance) {
		this(clazz);

		this.instance = instance;
	}

	public Class<? extends T> getClazz() {
		return clazz;
	}

	public T getInstance() {
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

	public boolean isResolved() {
		return pendingDependencies.isEmpty();
	}

	/**
	 * Returns the currently pending dependencies, e.g. the missing capability classes.
	 */
	public Collection<Class<? extends ICapability>> getPendingClasses() {
		return new HashSet<Class<? extends ICapability>>(pendingDependencies.keySet());
	}

	/**
	 * Returns the currently resolved dependencies, e.g. the already initialized capability classes.
	 */
	public Collection<Class<? extends ICapability>> getResolvedClasses() {
		return new HashSet<Class<? extends ICapability>>(resolvedDependencies.keySet());
	}

	/**
	 * Resolves all dependencies that can be satisfied by the given {@link CapabilityInstance}.
	 */
	public <D extends ICapability> void resolve(CapabilityInstance potentialDependency) {

		for (Class<? extends ICapability> capabilityClass : potentialDependency.getCapabilities()) {

			if (pendingDependencies.containsKey(capabilityClass)) {
				Field field = pendingDependencies.get(capabilityClass);

				try {
					// Initialize the field of the application or capability
					// TODO Security implications?
					field.setAccessible(true);
					field.set(getInstance(), potentialDependency.getProxy());

					resolve(capabilityClass);
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

	/**
	 * Unresolves all dependencies that are being resolved with the given {@link CapabilityInstance}.
	 */
	public <D extends ICapability> void unresolve(CapabilityInstance potentialDependency) {

		for (Class<? extends ICapability> capabilityClass : potentialDependency.getCapabilities()) {

			if (resolvedDependencies.containsKey(capabilityClass)) {
				Field field = resolvedDependencies.get(capabilityClass);

				try {
					// TODO Security implications?
					field.setAccessible(true);
					if (field.get(getInstance()) == potentialDependency.getProxy()) {
						// dependency is being resolved with potentialDependency
						field.set(getInstance(), null);
						unresolve(capabilityClass);
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

	}

	/**
	 * Unresolves all currently resolved dependencies
	 */
	public <D extends ICapability> void unresolveAllDependencies() {
		// Iterator-safe implementation for the following:
		// for (Class<? extends ICapability> clazz : resolvedDependencies.keySet()){ unresolve(clazz); }
		// Due to unresolve producing changes in the map that backs up the foreach iterator, commented code is not safe

		for (Iterator<Class<? extends ICapability>> it = resolvedDependencies.keySet().iterator(); it.hasNext();) {
			// unresolve(it.next())
			Class<? extends ICapability> clazz = it.next();
			Field field = resolvedDependencies.get(clazz);
			it.remove();
			pendingDependencies.put(clazz, field);
		}
	}

	/**
	 * Updates the internal dependency state.
	 */
	protected void resolve(Class<? extends ICapability> capabilityClass) {
		Field field = pendingDependencies.remove(capabilityClass);
		resolvedDependencies.put(capabilityClass, field);
	}

	/**
	 * Updates the internal dependency state.
	 */
	protected void unresolve(Class<? extends ICapability> capabilityClass) {
		Field field = resolvedDependencies.remove(capabilityClass);
		pendingDependencies.put(capabilityClass, field);
	}

	/**
	 * Collects and returns all fields in the given class, which have the @DependingOn annotation.
	 */
	protected Map<Class<? extends ICapability>, Field> getDependencies(Class<? extends T> clazz) {
		Map<Class<? extends ICapability>, Field> dependencies = new HashMap<Class<? extends ICapability>, Field>();

		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(DependingOn.class)) {

				if (!(ICapability.class.isAssignableFrom(field.getType()))) {
					throw new IllegalArgumentException(
							"In " + clazz.getName() + " " + field.getType().getName() + " does not implement " + ICapability.class.getName() +
									" and can therefore not be used as a dependency.");
				}

				// The following cast is safe, because it was explicitly checked above
				@SuppressWarnings("unchecked")
				Class<? extends ICapability> type = (Class<? extends ICapability>) field.getType();

				dependencies.put(type, field);
			}
		}

		return dependencies;
	}

}
