package org.mqnaas.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.scheduling.ServiceExecution;
import org.mqnaas.core.impl.scheduling.ScheduledJob;
import org.powermock.api.mockito.PowerMockito;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.TriggerFiredBundle;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ScheduledJobTest {

	ScheduledJob		scheduledJob;
	IExecutionService	executionService;
	ServiceExecution	serviceExecution;
	IService			sampleService;

	@Before
	public void prepareTest() {

		executionService = PowerMockito.mock(IExecutionService.class);
		scheduledJob = new ScheduledJob();
		sampleService = new SampleService();

		serviceExecution = new ServiceExecution();
		serviceExecution.setService(sampleService);

	}

	@Test
	public void scheduledJobExecutionWithoutParamsTest() throws JobExecutionException {

		JobExecutionContext context = generateExecutionContext();
		scheduledJob.execute(context);

		Mockito.verify(executionService, Mockito.times(1)).execute(sampleService, null);

	}

	@Test
	public void scheduledJobExecutionWithParamsTest() throws JobExecutionException {

		Object[] parameters = new Object[2];
		parameters[0] = new String("param00");
		parameters[1] = new String("param01");
		serviceExecution.setParameters(parameters);

		JobExecutionContext context = generateExecutionContext();

		scheduledJob.execute(context);

		Mockito.verify(executionService, Mockito.times(1)).execute(sampleService, parameters);

	}

	/**
	 * Test checks the {@link ScheduledJob#execute(JobExecutionContext)} method fails when there's no {@link IExecutionService} in the job context.
	 * 
	 * @throws JobExecutionException
	 */
	@Test(expected = JobExecutionException.class)
	public void noExecutionServiceTest() throws JobExecutionException {

		JobExecutionContext context = generateExecutionContext();
		context.getJobDetail().getJobDataMap().remove(ScheduledJob.EXECUTION_SERVICE_KEY);

		scheduledJob.execute(context);

	}

	/**
	 * Test checks the {@link ScheduledJob#execute(JobExecutionContext)} method fails when there's no {@link ServiceExecution} in the job context.
	 * 
	 * @throws JobExecutionException
	 */
	@Test(expected = JobExecutionException.class)
	public void noServiceExecutionTest() throws JobExecutionException {

		JobExecutionContext context = generateExecutionContext();
		context.getJobDetail().getJobDataMap().remove(ScheduledJob.SERVICE_EXECUTION_KEY);

		scheduledJob.execute(context);

	}

	/**
	 * Test checks the {@link ScheduledJob#execute(JobExecutionContext)} method fails when there's no {@link IService} in the {@link ServiceExecution}
	 * of the job context.
	 * 
	 * @throws JobExecutionException
	 */
	@Test(expected = JobExecutionException.class)
	public void noServiceTest() throws JobExecutionException {

		JobExecutionContext context = generateExecutionContext();
		context.getJobDetail().getJobDataMap().remove(ScheduledJob.SERVICE_EXECUTION_KEY);
		context.getJobDetail().getJobDataMap().put(ScheduledJob.SERVICE_EXECUTION_KEY, new ServiceExecution());
		scheduledJob.execute(context);

	}

	private JobExecutionContext generateExecutionContext() {

		Map<String, Object> map = new HashMap<String, Object>();
		JobDataMap jobDataMap = new JobDataMap(map);
		jobDataMap.put(ScheduledJob.EXECUTION_SERVICE_KEY, executionService);
		jobDataMap.put(ScheduledJob.SERVICE_EXECUTION_KEY, serviceExecution);

		JobDetail jobDetail = new JobDetail();
		jobDetail.setJobDataMap(jobDataMap);

		Trigger trigger = new SimpleTrigger();

		TriggerFiredBundle triggerBundle = new TriggerFiredBundle(jobDetail, trigger, null, false, null, null, null, null);

		return new JobExecutionContext(null, triggerBundle, scheduledJob);

	}

}
