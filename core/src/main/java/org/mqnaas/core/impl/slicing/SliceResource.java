package org.mqnaas.core.impl.slicing;

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;

/**
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class SliceResource implements IResource {

	private static AtomicInteger	ID_COUNTER	= new AtomicInteger();

	private String					id;

	public SliceResource() {
		id = "slice-" + ID_COUNTER.incrementAndGet();
	}

	@Override
	public String getId() {
		return id;
	}

}
