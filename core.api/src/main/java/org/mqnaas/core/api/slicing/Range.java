package org.mqnaas.core.api.slicing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 * 
 */
@XmlRootElement(namespace = "org.mqnaas")
@XmlAccessorType(XmlAccessType.FIELD)
public class Range implements Serializable {

	private static final long	serialVersionUID	= -5198906887686759170L;

	@XmlElement(required = true)
	private int					lowerBound;
	@XmlElement(required = true)
	private int					upperBound;

	public Range() {

	}

	public Range(int lowerBound, int upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(int lowerBound) {
		this.lowerBound = lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(int upperBound) {
		this.upperBound = upperBound;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lowerBound;
		result = prime * result + upperBound;
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
		Range other = (Range) obj;
		if (lowerBound != other.lowerBound)
			return false;
		if (upperBound != other.upperBound)
			return false;
		return true;
	}
	
	public int size() {
		return upperBound - lowerBound + 1;
	}


	@Override
	public String toString() {
		return "Range [" + lowerBound + "-" + upperBound + "]";
	}

}
