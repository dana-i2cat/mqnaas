package org.mqnaas.core.api;

/*
 * #%L
 * MQNaaS :: Core.API
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

/**
 * A simple store for String values which can be bound to any {@link IResource} if necessary.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IAttributeStore extends ICapability {

	/**
	 * This static can be used by all modules defining a map between an OpenNaaS {@link IResource} and an external component.
	 */
	static final String	RESOURCE_EXTERNAL_ID	= "resource.external.id";

	static final String	RESOURCE_EXTERNAL_NAME	= "resource.external.name";

	String getAttribute(String name);

	void setAttribute(String name, String value);

}
