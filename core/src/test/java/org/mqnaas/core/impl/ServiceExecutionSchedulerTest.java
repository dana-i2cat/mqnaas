package org.mqnaas.core.impl;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.ServiceExecution;
import org.mqnaas.core.api.Trigger;
import org.mqnaas.core.api.exceptions.ServiceExecutionSchedulerException;
import org.mqnaas.test.helpers.ReflectionTestHelper;
import org.powermock.api.mockito.PowerMockito;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ServiceExecutionSchedulerTest {

	IExecutionService			executionService;
	ServiceExecutionScheduler	scheduler;
	ServiceExecution			serviceExecution;
	IService					service;
	Trigger						trigger;

	@Before
	public void prepareTest() throws SecurityException, IllegalArgumentException, IllegalAccessException {

		scheduler = new ServiceExecutionScheduler();

		executionService = PowerMockito.mock(IExecutionService.class);
		ReflectionTestHelper.injectPrivateField(scheduler, executionService, "executionService");

		scheduler.activate();

		service = new SampleService();

		trigger = new BasicTrigger();
		((BasicTrigger) trigger).setStartDate(new Date(System.currentTimeMillis() + 3000L));

		serviceExecution = new ServiceExecution();
		serviceExecution.setService(service);
		serviceExecution.setTrigger(trigger);

	}

	@After
	public void shutdownTest() {
		scheduler.deactivate();
	}

	@Test
	public void correctSchedulingTest() throws ServiceExecutionSchedulerException, InterruptedException {

		Assert.assertTrue(scheduler.getScheduledServiceExecutions().isEmpty());

		scheduler.schedule(serviceExecution);

		Assert.assertTrue(1 == scheduler.getScheduledServiceExecutions().size());

		Thread.sleep(3000);

		Assert.assertTrue(scheduler.getScheduledServiceExecutions().isEmpty());
	}

	/**
	 * Test checks that the {@link ServiceExecutionScheduler} fails when the there's no provided {@link ServiceExecution}
	 * 
	 * @throws ServiceExecutionSchedulerException
	 */
	@Test(expected = ServiceExecutionSchedulerException.class)
	public void nullServiceExecutionTest() throws ServiceExecutionSchedulerException {

		scheduler.schedule(null);

	}

	/**
	 * Test checks that the {@link ServiceExecutionScheduler} fails when there's no provided {@link Trigger}
	 * 
	 * @throws ServiceExecutionSchedulerException
	 */
	@Test(expected = ServiceExecutionSchedulerException.class)
	public void noTriggerExecutionTest() throws ServiceExecutionSchedulerException {

		serviceExecution.setTrigger(null);
		scheduler.schedule(serviceExecution);

	}

	/**
	 * Test checks that the {@link ServiceExecutionScheduler} fails when there's no provided {@link IService}
	 * 
	 * @throws ServiceExecutionSchedulerException
	 */
	@Test(expected = ServiceExecutionSchedulerException.class)
	public void noServiceExecutionTest() throws ServiceExecutionSchedulerException {

		serviceExecution.setService(null);
		scheduler.schedule(serviceExecution);

	}
}
