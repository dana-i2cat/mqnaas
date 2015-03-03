package org.mqnaas.core.impl.scheduling;

/*
 * #%L
 * MQNaaS :: Core
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

import java.lang.reflect.InvocationTargetException;

import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.scheduling.ServiceExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Implementation of the {@link Job} Quartz interface.
 * </p>
 * <p>
 * The <code>SchedledJob</code> are the scheduled tasks being executed by the Quartz library inside MqNaaS. They contain the {@link ServiceExecution}
 * inside the job context in order to execute a {@link IService} through the {@link IExecutionService}. Optionally they could contain a reference to a
 * {@link IServiceExecutionCallback} to be called when the {@link IExecutionService} finishes the {@link IService} execution.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ScheduledJob implements Job {

	public static final String	SERVICE_EXECUTION_KEY			= "ServiceExecution";
	public static final String	EXECUTION_SERVICE_KEY			= "ExecutionService";
	public static final String	SERVICE_EXECUTION_CALLBACK_KEY	= "ServiceExecutionCallback";

	private static final Logger	log								= LoggerFactory.getLogger(ScheduledJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

		// The execution service can not be injected by constructor since it's not supported by the Quartz job factories.
		IExecutionService executionService = (IExecutionService) jobDataMap.get(IExecutionService.class.getName());
		ServiceExecution serviceExecution = (ServiceExecution) jobDataMap.get(ServiceExecution.class.getName());
		IServiceExecutionCallback serviceExecutionCallback = (IServiceExecutionCallback) jobDataMap.get(IServiceExecutionCallback.class.getName());

		Key jobKey = context.getJobDetail().getKey();

		if (executionService == null)
			throw new JobExecutionException(
					"Scheduled Job \"" + jobKey.getGroup() + ":" + jobKey.getName() + "\" could not be executed: There's no ExecutionService defined in job's context");

		if (serviceExecution == null)
			throw new JobExecutionException(
					"Scheduled Job \"" + jobKey.getGroup() + ":" + jobKey.getName() + "\" could not be executed: There's no ServiceExecution defined in job's context");

		if (serviceExecution.getService() == null)
			throw new JobExecutionException(
					"Scheduled Job \"" + jobKey.getGroup() + ":" + jobKey.getName() + "\" could not be executed: There's no IService defined in job's context");

		log.debug("Executing scheduled service [" + serviceExecution.getService().getClass().getName() + "]");
		try {
			executionService.execute(serviceExecution.getService(), serviceExecution.getParameters());

			if (serviceExecutionCallback != null) {
				log.debug("Calling callback service for job \"" + jobKey.getGroup() + ":" + jobKey.getName() + "\" and service [" + serviceExecution
						.getService().getClass().getName() + "]");

				serviceExecutionCallback.serviceExecutionFinished(serviceExecution);
			}
			else
				log.debug("No callback service defined for job \"" + jobKey.getGroup() + ":" + jobKey.getName() + "\" and service [" + serviceExecution
						.getService().getClass().getName() + "]");
		} catch (InvocationTargetException e) {
			throw new JobExecutionException(e.getCause());
		}
	}
}
