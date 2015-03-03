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

import java.util.UUID;

import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.scheduling.ServiceExecution;
import org.mqnaas.core.impl.utils.ClassMap;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

/**
 * <p>
 * Builder for the {@link JobDetail} Quartz class.
 * </p>
 * <p>
 * The <code>JobDetail</code> is necessary for scheduling jobs to Quartz. It contains the job to be executed, and the parameters to be used by this
 * job. The {@link JobDetailBuilder} provides helping methods to introduce into the <code>JobDetail</code> the parameters used by {@link ScheduledJob}
 * class.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class JobDetailBuilder {

	private ClassMap	jobData;

	public JobDetailBuilder() {
		jobData = new ClassMap();
	}

	public JobDetail build() {

		JobDetail jobDetail = new JobDetail();
		JobDataMap dataMap = new JobDataMap();

		for (Class<?> c : jobData.getAll().keySet())
			dataMap.put(c.getName(), jobData.get(c));

		jobDetail.setJobClass(ScheduledJob.class);
		jobDetail.setJobDataMap(dataMap);

		// Quartz 2 generates randoms ids for the jobs. In Quartz 1, it's mandatory the user defines it.
		jobDetail.setName(UUID.randomUUID().toString());

		return jobDetail;
	}

	/**
	 * It would add the <code>executionService</code> to the {@link JobDetail} when it's built.
	 * 
	 * @param executionService
	 * @return
	 */
	public JobDetailBuilder withExecutionService(IExecutionService executionService) {
		jobData.put(IExecutionService.class, executionService);
		return this;
	}

	/**
	 * It would add the <code>serviceExecution</code> to the {@link JobDetail} when it's built.
	 * 
	 * @param serviceExecution
	 * @return
	 */
	public JobDetailBuilder withServiceExecution(ServiceExecution serviceExecution) {
		jobData.put(ServiceExecution.class, serviceExecution);
		return this;
	}

	/**
	 * It would add the <code>serviceExecutionCallback</code> to the {@link JobDetail} when it's built.
	 * 
	 * @param serviceExecution
	 * @return
	 */
	public JobDetailBuilder withServiceExecutionCallback(IServiceExecutionCallback serviceExecutionCallback) {
		jobData.put(IServiceExecutionCallback.class, serviceExecutionCallback);
		return this;
	}
}
