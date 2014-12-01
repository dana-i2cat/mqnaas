package org.mqnaas.core.api.slicing;

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 * 
 */
@XmlRootElement(namespace = "org.mqnaas")
@XmlAccessorType(XmlAccessType.FIELD)
public class SliceCube implements Serializable {

	private static final long	serialVersionUID	= -1939551413576294335L;

	@XmlElementWrapper(name = "ranges")
	@XmlElement(required = true, name = "range")
	private Range[]				ranges;

	// no-arg constructor for JAXB
	public SliceCube() {
	}

	public SliceCube(Range[] ranges) {
		this.ranges = ranges;
	}

	public Range[] getRanges() {
		return ranges;
	}

	public void setRanges(Range[] ranges) {
		this.ranges = ranges;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(ranges);
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
		SliceCube other = (SliceCube) obj;
		if (!Arrays.equals(ranges, other.ranges))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SliceCube [ranges=" + Arrays.toString(ranges) + "]";
	}

}
