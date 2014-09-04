package org.mqnaas.core.impl.utils;

import java.util.HashMap;
import java.util.Map;

import org.mqnaas.core.api.ServiceExecution;
import org.mqnaas.core.impl.BasicTrigger;
import org.mqnaas.core.impl.ScheduledJob;
import org.mqnaas.core.impl.ServiceExecutionScheduler;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * Utilities for the {@link ServiceExecutionScheduler}
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public abstract class SchedulerUtils {

	public static Trigger createServiceExecutionSchedulerInternalTrigger(JobDetail jobDetail) {

		Trigger trigger = null;

		ServiceExecution serviceExecution = (ServiceExecution) jobDetail.getJobDataMap().get(ScheduledJob.SERVICE_EXECUTION_KEY);

		if (serviceExecution.getTrigger() instanceof BasicTrigger)
			trigger = TriggerBuilder.newTrigger().forJob(jobDetail).startAt(((BasicTrigger) serviceExecution.getTrigger()).getStartDate()).build();

		// TODO add support for other types of trigger: periodical jobs, etc.

		return trigger;

	}

	public static JobDetail createServiceExecutionSchedulerJobDetail(ServiceExecution serviceExecution) {

		Map<String, Object> map = new HashMap<String, Object>();
		JobDataMap jobDataMap = new JobDataMap(map);
		jobDataMap.put(ScheduledJob.SERVICE_EXECUTION_KEY, serviceExecution);

		JobDetail jobDetail = JobBuilder.newJob(ScheduledJob.class).usingJobData(jobDataMap).build();

		return jobDetail;
	}

}
