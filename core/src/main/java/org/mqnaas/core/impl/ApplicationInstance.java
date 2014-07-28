package org.mqnaas.core.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mqnaas.core.api.IApplication;
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

	private IExecutionService					executionService;

	private ApplicationProxyHolder				proxyHolder;

	private ApplicationInstanceLifeCycleState	state;

	public ApplicationInstance(Class<? extends IApplication> clazz) {
		this(clazz, null);

	}

	public ApplicationInstance(Class<? extends IApplication> clazz, IApplication instance) {
		super(clazz);

		this.instance = instance;

		setState(ApplicationInstanceLifeCycleState.INSTANTIATED);

		proxyHolder = new ApplicationProxyHolder(clazz, getInstance());
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

}
