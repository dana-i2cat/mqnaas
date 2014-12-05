package org.mqnaas.network.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.Specification.Type;

public class RequestRootResource implements IResource {
	
	private static AtomicInteger ID_COUNTER = new AtomicInteger();

	private String id;

	private Type type;

	public RequestRootResource(Type type) {
		id = "req-" + ID_COUNTER.incrementAndGet();
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String getId() {
		return id;
	}

}