package org.mqnaas.core.api;

/**
 * <p>
 * <code>IExecutionService</code> is one of the core capabilities of OpenNaaS.
 * </p>
 * 
 * <p>
 * It offers two different types of services:
 * <ul>
 * <li><b>Execution services</b>: These services execute services transactionally considering security and concurrency. After successful service
 * execution, service observers are notified.</li>
 * <li><b>Observation services</b>: These services allow for the registration of services to observe the service execution.</li>
 * </ul>
 * 
 */
public interface IExecutionService extends ICapability {

	/**
	 * <p>
	 * Executes the given {@link IService} synchronously with the given parameters and returns the result.
	 * </p>
	 * 
	 * <p>
	 * The execution involves the following steps:
	 * <ol>
	 * <li>check if the identity running the current thread has sufficient permissions to execute the service</li>
	 * <li>acquire a lock on the resource to which the service is bound,</li>
	 * <li>assure execution within a transaction:
	 * <ul>
	 * <li>if <u>no transaction</u> exists: open a new transaction</li>
	 * <li>if a <u>transaction already</u> exists: join this transaction</li>
	 * </ul>
	 * <li>execute the service</li>
	 * <li>notify all registered services observing the service executed</li>
	 * </ol>
	 * 
	 * @param service
	 *            The service to be executed
	 * @param parameters
	 *            The parameters with which to execute the given service
	 * 
	 * @return The result of the service execution
	 */
	Object execute(IService service, Object[] parameters);

	/**
	 * <p>
	 * Adds an observation: Whenever the given {@link IObservationFilter} applies, the given {@link IService} is executed.
	 * </p>
	 * <p>
	 * Whenever a service is executed (see {@link #execute(IService, Object[])} for a detailed explanation), the final step is to notify services
	 * observing other services. Which other services are observed by a service is defined by means of the {@link IObservationFilter} registered
	 * together with the observing service.
	 * 
	 * @param filter
	 *            The {@link IObservationFilter}, which determines which services are observed
	 * @param service
	 *            The {@link IService} to be executed if the filter applies
	 */
	void registerObservation(IObservationFilter filter, IService service);

}
