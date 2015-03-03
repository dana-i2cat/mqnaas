package org.mqnaas.core.impl;

/*
 * #%L
 * MQNaaS :: Core
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
