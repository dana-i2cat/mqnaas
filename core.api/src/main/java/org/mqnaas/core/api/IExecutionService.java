package org.mqnaas.core.api;

/**
 * <p>
 * <code>IExecutionService</code> is one of the core capabilities of MQNaaS.
 * </p>
 * 
 * <p>
 * It offers the ability of executing services. It executes services transactionally considering security and concurrency.
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

}
