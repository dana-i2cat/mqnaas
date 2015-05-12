package org.mqnaas.core.impl;

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
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.scheduling.ServiceExecution;
import org.mqnaas.core.api.scheduling.Trigger;
import org.mqnaas.core.impl.samples.SampleService;
import org.mqnaas.core.impl.scheduling.ScheduledJob;
import org.mqnaas.core.impl.scheduling.TriggerFactory;
import org.powermock.api.mockito.PowerMockito;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleTrigger;
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
	Trigger				trigger;

	@Before
	public void prepareTest() {

		executionService = PowerMockito.mock(IExecutionService.class);
		scheduledJob = new ScheduledJob();
		sampleService = new SampleService();
		trigger = TriggerFactory.create(null);
		serviceExecution = new ServiceExecution(sampleService, trigger);

	}

	@Test
	public void scheduledJobExecutionWithoutParamsTest() throws JobExecutionException, InvocationTargetException {

		JobExecutionContext context = generateExecutionContext();
		scheduledJob.execute(context);

		Mockito.verify(executionService, Mockito.times(1)).execute(sampleService, null);

	}

	@Test
	public void scheduledJobExecutionWithParamsTest() throws JobExecutionException, InvocationTargetException {

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
		context.getJobDetail().getJobDataMap().remove(IExecutionService.class.getName());

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
		context.getJobDetail().getJobDataMap().remove(ServiceExecution.class.getName());

		scheduledJob.execute(context);

	}

	/**
	 * Test checks the {@link ScheduledJob#execute(JobExecutionContext)} method fails when there's no {@link IService} in the {@link ServiceExecution}
	 * of the job context.
	 * 
	 * @throws JobExecutionException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void noServiceTest() throws JobExecutionException {

		JobExecutionContext context = generateExecutionContext();
		context.getJobDetail().getJobDataMap().remove(ServiceExecution.class.getName());
		context.getJobDetail().getJobDataMap().put(ServiceExecution.class.getName(), new ServiceExecution(null, trigger));
		scheduledJob.execute(context);

	}

	private JobExecutionContext generateExecutionContext() {

		Map<String, Object> map = new HashMap<String, Object>();
		JobDataMap jobDataMap = new JobDataMap(map);
		jobDataMap.put(IExecutionService.class.getName(), executionService);
		jobDataMap.put(ServiceExecution.class.getName(), serviceExecution);

		JobDetail jobDetail = new JobDetail();
		jobDetail.setJobDataMap(jobDataMap);

		org.quartz.Trigger quartzTrigger = new SimpleTrigger();

		TriggerFiredBundle triggerBundle = new TriggerFiredBundle(jobDetail, quartzTrigger, null, false, null, null, null, null);

		return new JobExecutionContext(null, triggerBundle, scheduledJob);

	}

}
