package org.mqnaas.core.api;

/**
 * <p>
 * <code>IServiceExecutionScheduler</code> is one of the core capabilities of MQNaaS.
 * </p>
 * <p>
 * It offers the ability of scheduling the execution of {@link IService services}, both periodically and in a specific date.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface IServiceExecutionScheduler extends ICapability {

	public void schedule(ServiceExecution serviceExecution);

	public void cancel(ServiceExecution serviceExecution);

}
