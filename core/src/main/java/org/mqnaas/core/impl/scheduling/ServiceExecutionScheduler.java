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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.ServiceExecutionSchedulerException;
import org.mqnaas.core.api.scheduling.IServiceExecutionScheduler;
import org.mqnaas.core.api.scheduling.ServiceExecution;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Implementation of the {@link IServiceExecutionScheduler} using the Quartz library.
 * </p>
 * <p>
 * The <code>ServiceExecutionScheduler</code> uses an internal scheduler provided by the Quartz library in order to schedule jobs executions. The
 * details of the job being executed are provided by the {@link ServiceExecution} class, which contains the {@link IService} being executed and the
 * {@link Trigger} defining the execution date.
 * </p>
 * <p>
 * The <code>ServiceExecutionScheduler</code> internally creates instances of the {@link ScheduledJob} class from the provided
 * <code>ServiceExecution</code>, which are the jobs scheduled in the {@link Scheduler Quartz scheduler}.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 * 
 */
public class ServiceExecutionScheduler implements IServiceExecutionScheduler {

	private static final Logger			log	= LoggerFactory.getLogger(ServiceExecutionScheduler.class);

	@DependingOn(core = true)
	private IExecutionService			executionService;

	private Scheduler					quartzScheduler;
	private Map<ServiceExecution, Key>	scheduledJobs;
	private IServiceExecutionCallback	serviceExecutionCallback;

	class ServiceExecutionCallback implements IServiceExecutionCallback {

		/**
		 * Checks the instance type of the {@link ServiceExecution#getTrigger() trigger} and decides if the <code>serviceExecution</code> should be
		 * removed from the <code>schedyledJobs</code> map or not.
		 */
		@Override
		public void serviceExecutionFinished(ServiceExecution serviceExecution) {
			if (serviceExecution.getTrigger() instanceof BasicTrigger)
				scheduledJobs.remove(serviceExecution);
		}

	}

	public static boolean isSupporting(IRootResource resource) {
		return resource.getDescriptor().getSpecification().getType().equals(Specification.Type.CORE);
	}

	@Override
	public void activate() throws ApplicationActivationException {

		scheduledJobs = new HashMap<ServiceExecution, Key>();
		serviceExecutionCallback = new ServiceExecutionCallback();

		try {
			quartzScheduler = StdSchedulerFactory.getDefaultScheduler();
			quartzScheduler.start();

		} catch (SchedulerException e) {
			log.error("Could not initialize ServiceExecutionScheduler internal scheduler.", e);
			throw new ApplicationActivationException(e);
		}
	}

	@Override
	public void deactivate() {
		// FIXME should deactivate() method launch an exception when there's an error deactivating the application ??

		try {
			quartzScheduler.shutdown();
		} catch (SchedulerException e) {
			log.error("Could not shut down ServiceExecutionScheduler internal scheduler.", e);

		}

	}

	@Override
	public void schedule(ServiceExecution serviceExecution) throws ServiceExecutionSchedulerException {

		if (serviceExecution == null)
			throw new IllegalArgumentException("Could not schedule service execution: ServiceExecution is null");

		log.debug("Scheduling new Service Execution for service [" + serviceExecution.getService().getClass().getName() + "]");

		try {

			JobDetail jobDetail = new JobDetailBuilder().withExecutionService(executionService).withServiceExecution(serviceExecution)
					.withServiceExecutionCallback(serviceExecutionCallback).build();

			Trigger trigger = new TriggerBuilder().usingTrigger(serviceExecution.getTrigger()).build();

			quartzScheduler.scheduleJob(jobDetail, trigger);

			scheduledJobs.put(serviceExecution, jobDetail.getKey());

		} catch (SchedulerException e) {
			log.error("Could not schedule service execution: ", e);
			throw new ServiceExecutionSchedulerException(e);
		}

		log.info("Service Execution scheduled for service [" + serviceExecution.getService().getClass().getName() + "]");

	}

	@Override
	public void cancel(ServiceExecution serviceExecution) throws ServiceExecutionSchedulerException {

		log.debug("Cancelling scheduled service execution");

		try {
			Key jobKey = scheduledJobs.get(serviceExecution);
			quartzScheduler.deleteJob(jobKey.getName(), jobKey.getGroup());

			scheduledJobs.remove(serviceExecution);

		} catch (SchedulerException e) {
			log.error("Could not cancel scheduled service execution: ", e);
			throw new ServiceExecutionSchedulerException(e);

		}

		log.info("Scheduled service execution canceled for service [" + serviceExecution.getService().getClass().getName() + "]");
	}

	@Override
	public Set<ServiceExecution> getScheduledServiceExecutions() {
		return scheduledJobs.keySet();
	}

}
