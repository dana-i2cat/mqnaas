package org.mqnaas.core.api;

/**
 * <p>
 * Interface defining callback methods for a service execution.
 * </p>
 * 
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface IServiceExecutionCallback {

	public void serviceExecutionFinished(ServiceExecution serviceExecution);

}
