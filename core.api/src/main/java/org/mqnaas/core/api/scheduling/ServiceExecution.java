package org.mqnaas.core.api.scheduling;

/*
 * #%L
 * MQNaaS :: Core.API
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.util.Arrays;

import org.mqnaas.core.api.IService;

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

	public ServiceExecution(IService service, Trigger trigger) {
		if (service == null || trigger == null)
			throw new IllegalArgumentException("Service and trigger should not be null");

		this.service = service;
		this.trigger = trigger;
	}

	public IService getService() {
		return service;
	}

	public void setService(IService service) {
		if (service == null)
			throw new IllegalArgumentException("Service should not be null");

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
		if (trigger == null)
			throw new IllegalArgumentException("Trigger should not be null");
		this.trigger = trigger;
	}

	@Override
	public String toString() {
		return "ServiceExecution [service=" + service + ", parameters=" + Arrays.toString(parameters) + ", trigger=" + trigger + "]";
	}

}
