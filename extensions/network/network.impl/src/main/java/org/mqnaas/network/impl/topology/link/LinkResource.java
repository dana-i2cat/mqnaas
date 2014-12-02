package org.mqnaas.network.impl.topology.link;

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;

/**
 * Basic link resource implementation providing a simple unique id.
 * 
 * @author Georg Mansky-Kummert
 */
public class LinkResource implements IResource {

	private static AtomicInteger ID_COUNTER = new AtomicInteger();
	
	private String id;

	public LinkResource() {
		id = "link-" + ID_COUNTER.incrementAndGet();
	}
	
	@Override
	public String getId() {
		return id;
	}
	
}
