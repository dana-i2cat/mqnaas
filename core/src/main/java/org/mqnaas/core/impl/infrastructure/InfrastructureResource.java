package org.mqnaas.core.impl.infrastructure;

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;

/**
 * Basic infrastructure resource implementation providing a simple unique id.
 * 
 * @author Georg Mansky-Kummert
 */
public class InfrastructureResource implements IResource {

	private static AtomicInteger ID_COUNTER = new AtomicInteger();

	private String id;

	public InfrastructureResource() {
		id = "ifs-" + ID_COUNTER.incrementAndGet();
	}

	@Override
	public String getId() {
		return id;
	}

}
