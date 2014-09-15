package org.mqnaas.core.api.scheduling;

import java.util.Date;

import org.mqnaas.core.api.IService;

/**
 * <p>
 * General interface representing the mechanism to schedule {@link IService} executions in MqNaaS.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public interface Trigger {

	public Date getStartDate();

	public Date getEndDate();

	// TODO add additional methods for repetition, intervals, etc.

}
