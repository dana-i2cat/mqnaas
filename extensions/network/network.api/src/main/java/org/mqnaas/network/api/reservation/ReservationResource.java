package org.mqnaas.network.api.reservation;

import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.mqnaas.core.api.IResource;

/**
 * Basic reservation resource implementation providing a simple unique id.
 * 
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlRootElement(namespace = "org.mqnaas")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReservationResource implements IResource {

	@XmlTransient
	private static AtomicInteger	ID_COUNTER	= new AtomicInteger();

	private String					id;

	public ReservationResource() {
		id = "reservation-" + ID_COUNTER.incrementAndGet();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ReservationResource [id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ReservationResource other = (ReservationResource) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
