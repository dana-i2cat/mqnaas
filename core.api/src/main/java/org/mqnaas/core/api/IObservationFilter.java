package org.mqnaas.core.api;

/**
 * <p>
 * An <code>IObservationFilter</code> is responsible for deciding whether a given {@link IService} should be monitored.
 * </p>
 * <p>
 * After all service executions in the core (see {@link IExecutionService} for details, registered <code>IObservationsFilter</code>s are utilized to
 * determine whether a service needs to be notified or not.
 * </p>
 * 
 * @see IExecutionService {@link IExecutionService#registerObservation(IObservationFilter, IService)} for details about its usage.
 * 
 */
public interface IObservationFilter {

	/**
	 * <p>
	 * Decide, whether the given {@link IService} is observed by this filter for the given execution parameters.
	 * <p>
	 * <p>
	 * If <code>true</code> is returned, the platform will launch the registered observation service. See
	 * {@link IExecutionService#registerObservation(IObservationFilter, IService)} for details.
	 * </p>
	 * 
	 * @param service
	 *            The service executed by the platform, which could be observed
	 * @param parameters
	 *            The parameters with which the service was executed
	 * 
	 * @return Whether this filter observes the given service
	 */
	boolean observes(IService service, Object[] parameters);

	/**
	 * <p>
	 * If {@link #observes(IService, Object[])} returns <code>true</code> this service is called to allow the implementor to modify the parameters,
	 * with which the observation service is called.
	 * </p>
	 * <b>TODO</b>: This should be moved as a parameter to {@link IExecutionService#registerObservation(IObservationFilter, IService)} .
	 * 
	 * @param service
	 *            The service, which was observed
	 * @param parameters
	 *            The parameters, with which the service was called
	 * @param result
	 *            The result of the observed service's execution
	 * 
	 * @return The reworked parameters for the observation service call
	 */
	Object[] getParameters(IService service, Object[] parameters, Object result);
}