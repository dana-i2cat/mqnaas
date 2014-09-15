package org.mqnaas.core.impl.scheduling;

import java.util.Date;

import org.mqnaas.core.api.scheduling.Trigger;

/**
 * <p>
 * Factory class to create MQNaaS {@link Trigger} instances.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class TriggerFactory {

	public static Trigger create(Date startDate) {
		return new BasicTrigger(startDate);
	}

}
