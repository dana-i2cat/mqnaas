package org.mqnaas.core.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResource;

/**
 * Implementation of {@link IAttributeStore} backed by a {@link ConcurrentHashMap} bound to all resources except the core.
 * 
 * @author Georg Mansky-Kummert
 */
public class AttributeStore implements IAttributeStore {

	private Map<String, String>	attributes;

	public static boolean isSupporting(IResource resource) {
		return resource.getClass().getName().equals("org.mqnaas.network.impl.topology.port.PortResource");
	}

	@Override
	public String getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public void setAttribute(String name, String value) {
		attributes.put(name, value);
	}

	@Override
	public void activate() {
		attributes = new ConcurrentHashMap<String, String>();
	}

	@Override
	public void deactivate() {
	}

}
