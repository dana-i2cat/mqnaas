package org.mqnaas.core.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link IAttributeStore} backed by a {@link ConcurrentHashMap} bound to all resources except the core.
 * 
 * @author Georg Mansky-Kummert
 */
public class AttributeStore implements IAttributeStore {

	private static final Logger	log	= LoggerFactory.getLogger(AttributeStore.class);

	private Map<String, String>	attributes;

	@Resource
	IResource					resource;

	public static boolean isSupporting(IResource resource) {
		return true;
	}

	public static boolean isSupporting(IRootResource resource) {
		return true;
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
	public void activate() throws ApplicationActivationException {
		log.info("Initializing AttributeStore capability for resource " + resource.getId());
		attributes = new ConcurrentHashMap<String, String>();
		log.info("Initialized AttributeStore capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing AttributeStore capability from resource " + resource.getId());
		log.info("Removed AttributeStore capability from resource " + resource.getId());

	}

}
