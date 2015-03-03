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
 * Simple implementation of the {@link Trigger} interface.
 * </p>
 * <p>
 * The <code>BasicTrigger</code> only contains the starting {@link Date} for the execution.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class BasicTrigger implements Trigger, Comparable<BasicTrigger> {

	private Date	startDate;

	public BasicTrigger(Date startDate) {
		this.startDate = startDate;
	}

	public BasicTrigger(Date startDate, Date endDate) {
		if (startDate.after(endDate))
			throw new IllegalArgumentException("StartDate can't be after the endDate.");
		this.startDate = startDate;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getEndDate() {
		return null;
	}

	@Override
	public int compareTo(BasicTrigger other) {
		return (this.startDate.compareTo(other.getStartDate()));
	}

	@Override
	public String toString() {
		return "BasicTrigger [startDate=" + startDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicTrigger other = (BasicTrigger) obj;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}
