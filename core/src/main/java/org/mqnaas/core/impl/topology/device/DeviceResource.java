package org.mqnaas.core.impl.topology.device;

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;

/**
 * Basic device resource implementation providing a simple unique id.
 * 
 * @author Georg Mansky-Kummert
 */
public class DeviceResource implements IResource {

	private static AtomicInteger ID_COUNTER = new AtomicInteger();
	
	private String id;
	
	public DeviceResource() {
		id = "dev-" + ID_COUNTER.incrementAndGet();
	}
	
	public String getId() {
		return id;
	}

}
