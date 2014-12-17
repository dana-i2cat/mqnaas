package org.mqnaas.network.impl.topology.link;

import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.mqnaas.core.api.IResource;

/**
 * Basic link resource implementation providing a simple unique id.
 * 
 * @author Georg Mansky-Kummert
 */
@XmlRootElement(namespace = "org.mqnaas")
@XmlAccessorType(XmlAccessType.FIELD)
public class LinkResource implements IResource {

	@XmlTransient
	private static AtomicInteger	ID_COUNTER	= new AtomicInteger();

	private String					id;

	public LinkResource() {
		id = "link-" + ID_COUNTER.incrementAndGet();
	}

	@Override
	public String getId() {
		return id;
	}

}
