package org.mqnaas.core.impl.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mqnaas.core.api.scheduling.ServiceExecution;
import org.mqnaas.core.impl.scheduling.BasicTrigger;
import org.mqnaas.core.impl.scheduling.ScheduledJob;
import org.mqnaas.core.impl.scheduling.ServiceExecutionScheduler;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

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

		// TODO add support for other types of trigger: periodical jobs, etc.
		if (serviceExecution.getTrigger() instanceof BasicTrigger) {
			trigger = new SimpleTrigger();
			trigger.setStartTime(((BasicTrigger) serviceExecution.getTrigger()).getStartDate());

		}
		// Quartz 2 generates randoms ids for the triggers. In Quartz 1, it's mandatory the user defines it.
		// We set the same as the job one to decrease collisions risks.
		trigger.setName(jobDetail.getName());

		return trigger;

	}

	public static JobDetail createServiceExecutionSchedulerJobDetail(ServiceExecution serviceExecution) {

		Map<String, Object> map = new HashMap<String, Object>();
		JobDataMap jobDataMap = new JobDataMap(map);
		jobDataMap.put(ScheduledJob.SERVICE_EXECUTION_KEY, serviceExecution);

		JobDetail jobDetail = new JobDetail();
		jobDetail.setJobDataMap(jobDataMap);
		jobDetail.setJobClass(ScheduledJob.class);

		// Quartz 2 generates randoms ids for the jobs. In Quartz 1, it's mandatory the user defines it.
		jobDetail.setName(UUID.randomUUID().toString());

		return jobDetail;
	}

}
