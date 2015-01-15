package org.mqnaas.core.api;

import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;

/**
 * <p>
 * <code>IApplication</code> is a marker interface to identify an MQNaaS application. Applications will be instantiated automatically by the platform,
 * therefore they need to have a constructor without arguments.
 * <p>
 * <p>
 * Applications may depend on capabilities which will be injected attributes annotated with {@link DependingOn}.
 * </p>
 */
public interface IApplication {

	/**
	 * Performs initialization required in order to have all its services available.
	 * 
	 * It is guaranteed that this method is called only when all dependencies (attributes annotated with {@link DependingOn}) have been injected.
	 * 
	 * The use of services provided by dependencies inside this method requires some care. In case of dependency cycles, it cannot be guaranteed that
	 * dependencies would have been activated before this method is called.
	 */
	void activate() throws ApplicationActivationException;

	/**
	 * Cleans initialization done in {@link IApplication#activate()}
	 * 
	 * Nothing can be granted regarding the availability of dependencies when this method is called. Dependencies may not be functional any more.
	 */
	void deactivate();

}
