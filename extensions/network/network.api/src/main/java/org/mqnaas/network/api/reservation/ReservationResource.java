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

}
