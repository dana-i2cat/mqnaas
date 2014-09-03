package org.mqnaas.core.api;

import java.util.Arrays;

/**
 * <p>
 * The <code>ServiceExecution</code> is the representation of a task to be scheduled by the {@link IServiceExecutionScheduler}
 * </p>
 * <p>
 * It consists of the {@link IService} to be executed, the parameters for this service execution, and a {@link Trigger}. The <code>trigger</code>
 * defines when this <code>IService</code> will be executed.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ServiceExecution {

	private IService	service;
	private Object[]	parameters;
	private Trigger		trigger;

	public IService getService() {
		return service;
	}

	public void setService(IService service) {
		this.service = service;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	@Override
	public String toString() {
		return "ServiceExecution [service=" + service + ", parameters=" + Arrays.toString(parameters) + ", trigger=" + trigger + "]";
	}

}
