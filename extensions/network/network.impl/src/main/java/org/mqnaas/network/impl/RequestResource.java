package org.mqnaas.network.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;

/**
 * Basic request resource implementation providing a simple unique id.
 * 
 * @author Georg Mansky-Kummert
 */

public class RequestResource implements IResource {
	
	private static AtomicInteger ID_COUNTER = new AtomicInteger();
	
	private String id;
	
	public RequestResource() {
		id = "req-" + ID_COUNTER.incrementAndGet();
	}

//	private Topology topology;
//
//	private Infrastructure infrastructure;
//	
//	private Map<IResource, Slice> slices = new HashMap<IResource, Slice>();
//
//	private Period period;
//
//	public Topology getTopology() {
//		return topology;
//	}
//
//	public Infrastructure getInfrastructure() {
//		return infrastructure;
//	}
//
//	public Period getPeriod() {
//		return period;
//	}
//	
//	public Slice getSlice(IResource resource) {
//		return slices.get(resource);
//	}

	@Override
	public String getId() {
		return id;
	}
}
