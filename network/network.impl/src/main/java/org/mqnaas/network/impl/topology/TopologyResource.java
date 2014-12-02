package org.mqnaas.network.impl.topology;

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;

/**
 * Basic topology resource implementation providing a simple unique id.
 * 
 * @author Georg Mansky-Kummert
 */
public class TopologyResource implements IResource {

	private static AtomicInteger ID_COUNTER = new AtomicInteger();

	private String id;

	public TopologyResource() {
		id = "topo-" + ID_COUNTER.incrementAndGet();
	}

	@Override
	public String getId() {
		return id;
	}

}
