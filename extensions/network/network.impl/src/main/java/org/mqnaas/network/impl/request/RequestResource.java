package org.mqnaas.network.impl.request;

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;

/**
 * Basic request resource implementation providing a simple unique id.
 * 
 * @author Georg Mansky-Kummert
 */

public class RequestResource implements IResource {

	private static AtomicInteger	ID_COUNTER	= new AtomicInteger();

	private String					id;

	public RequestResource() {
		id = "req-" + ID_COUNTER.incrementAndGet();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Request Resource [id=" + id + "]";
	}

}
