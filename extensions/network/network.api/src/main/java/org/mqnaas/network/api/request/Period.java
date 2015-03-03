package org.mqnaas.network.api.request;

/*
 * #%L
 * MQNaaS :: Network API
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Class representing a period of time, established by a start and an end date.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 *
 */
@XmlRootElement(namespace = "org.mqnaas")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "startDate", "endDate" })
public class Period implements Comparable<Period> {

	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date	startDate;
	@XmlJavaTypeAdapter(DateAdapter.class)
	private Date	endDate;

	// used by serialization framework
	Period() {
	}

	public Period(Date startDate, Date endDate) {

		if (startDate == null || endDate == null)
			throw new IllegalArgumentException("Start and end date should not be null");
		if (endDate.before(startDate))
			throw new IllegalArgumentException("Start date should be before the end date");

		this.startDate = startDate;
		this.endDate = endDate;

	}

	public Date getStartdate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @return <ul>
	 *         <li>"-1", if the starting and end date are smaller than the starting date of the <code>other</code> period.</li>
	 *         <li>"1", if the starting and end date are smaller than the end date of the <code>other</code>period.</li>
	 *         <li>"0", otherwise
	 *         </ul>
	 * 
	 */
	@Override
	public int compareTo(Period other) {

		// both are equals
		if (startDate.equals(other.getStartdate()) && endDate.equals(other.getEndDate()))
			return 0;

		// Both have same endDate, but different startDate
		if (endDate.equals(other.getEndDate())) {
			if (startDate.before(other.startDate))
				return -1;
			return 1;
		}

		// Both have same startDate, but different endDate
		if (startDate.equals(other.getEndDate())) {
			if (endDate.before(other.getEndDate()))
				return -1;
			return 1;
		}

		// They don't overlap
		if (startDate.after(other.getEndDate()))
			return 1;
		if (endDate.before(other.getStartdate()))
			return -1;

		// They overlap
		if (startDate.before(other.getStartdate()) && endDate.before(other.getEndDate()))
			return -1;
		return 1;
	}

	@Override
	public String toString() {
		return "Period [startDate=" + startDate + ", endDate=" + endDate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
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
		Period other = (Period) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
}