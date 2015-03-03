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

import org.apache.commons.lang3.StringUtils;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

/**
 * <p>
 * Builder for the {@link Trigger} Quartz class.
 * </p>
 * <p>
 * The <code>Trigger</code> is necessary for scheduling jobs to Quartz. It contains the specification of when and how would the job be executed. The
 * {@link TriggerBuilder} provides methods to introduce the trigger attributes from the MqNaaS {@link org.mqnaas.core.api.scheduling.Trigger}.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class TriggerBuilder {

	org.mqnaas.core.api.scheduling.Trigger	trigger;
	String									triggerName;

	public Trigger build() {

		Trigger quartzTrigger = null;

		if (trigger instanceof BasicTrigger) {
			quartzTrigger = new SimpleTrigger();
			quartzTrigger.setStartTime(((BasicTrigger) trigger).getStartDate());
		}

		// Quartz 2 generates randoms ids for the triggers. In Quartz 1, it's mandatory the user defines it.
		if (StringUtils.isEmpty(triggerName))
			triggerName = UUID.randomUUID().toString();

		quartzTrigger.setName(triggerName);

		return quartzTrigger;

	}

	public TriggerBuilder usingTrigger(org.mqnaas.core.api.scheduling.Trigger trigger) {
		this.trigger = trigger;

		return this;
	}

	public TriggerBuilder withName(String name) {
		this.triggerName = name;
		return this;
	}
}
