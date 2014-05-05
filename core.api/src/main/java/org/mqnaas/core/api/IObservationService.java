package org.mqnaas.core.api;

/**
 * 
 * <p>
 * <code>IObservationService</code> is one of the core capabilities of MQNaaS.
 * </p>
 * 
 * After successful service execution (see {@link IExecutionService}), service observers are notified. It allow the registration of services to
 * observe the service execution.
 */
public interface IObservationService extends ICapability {

	/**
	 * <p>
	 * Adds an observation: Whenever the given {@link IObservationFilter} applies, the given {@link IService} is executed.
	 * </p>
	 * <p>
	 * Whenever a service is executed (see {@link IExecutionService#execute(IService, Object[])} for a detailed explanation), the final step is to
	 * notify services observing other services. Which other services are observed by a service is defined by means of the {@link IObservationFilter}
	 * registered together with the observing service.
	 * 
	 * @param filter
	 *            The {@link IObservationFilter}, which determines which services are observed
	 * @param service
	 *            The {@link IService} to be executed if the filter applies
	 */
	void registerObservation(IObservationFilter filter, IService service);

}
