package org.mqnaas.network.impl.topology.device;

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;

/**
 * Basic port resource implementation providing a simple unique id.
 * 
 * @author Georg Mansky-Kummert
 */
public class PortResource implements IResource {

	private static AtomicInteger ID_COUNTER = new AtomicInteger();

	private String id;

	public PortResource() {
		id = "port-" + ID_COUNTER.incrementAndGet();
	}

	@Override
	public String getId() {
		return id;
	}

}
