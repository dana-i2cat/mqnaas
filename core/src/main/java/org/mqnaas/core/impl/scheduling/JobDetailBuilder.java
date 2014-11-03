package org.mqnaas.core.impl.scheduling;

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
