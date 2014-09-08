package org.mqnaas.core.impl.scheduling;

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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
