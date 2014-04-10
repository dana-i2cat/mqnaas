package org.opennaas.core.api;

import org.opennaas.core.annotation.DependingOn;

/**
 * <p>
 * <code>IApplication</code> is a marker interface to identify an OpenNaaS application. Applications
 * will be instantiated automatically by the platform, therefore they need to
 * have a constructor without arguments.
 * <p>
 * <p>
 * Applications may depend on capabilities which will be injected attributes
 * annotated with {@link DependingOn}.
 * </p>
 * 
 * TODO Define how the {@link #onDependenciesResolved()} can be removed and
 * replaced by a mechanism that can also be used to initialize capabilities.
 */
public interface IApplication {

	void onDependenciesResolved();

}
