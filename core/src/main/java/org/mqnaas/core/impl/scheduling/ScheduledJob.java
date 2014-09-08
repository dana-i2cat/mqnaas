package org.mqnaas.core.impl.scheduling;

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
		IExecutionService executionService = (IExecutionService) jobDataMap.get(EXECUTION_SERVICE_KEY);
		ServiceExecution serviceExecution = (ServiceExecution) jobDataMap.get(SERVICE_EXECUTION_KEY);
		IServiceExecutionCallback serviceExecutionCallback = (IServiceExecutionCallback) jobDataMap.get(SERVICE_EXECUTION_CALLBACK_KEY);

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

		executionService.execute(serviceExecution.getService(), serviceExecution.getParameters());

		if (serviceExecutionCallback != null) {
			log.debug("Calling callback service for job \"" + jobKey.getGroup() + ":" + jobKey.getName() + "\" and service [" + serviceExecution
					.getService().getClass().getName() + "]");

			serviceExecutionCallback.serviceExecutionFinished(serviceExecution);
		}
		else
			log.debug("No callback service defined for job \"" + jobKey.getGroup() + ":" + jobKey.getName() + "\" and service [" + serviceExecution
					.getService().getClass().getName() + "]");
	}
}
