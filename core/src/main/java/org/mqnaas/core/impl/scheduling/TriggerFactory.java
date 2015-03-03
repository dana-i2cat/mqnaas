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
