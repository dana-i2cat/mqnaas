package org.mqnaas.core.impl.resourcetree;

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

import org.mqnaas.core.impl.ApplicationInstance;
import org.mqnaas.core.impl.CapabilityInstance;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class CapabilityNode extends ApplicationNode {

	private ResourceNode	parent;

	public CapabilityNode() {
		super();
	}

	public CapabilityNode(CapabilityInstance ci) {
		super(ci);
	}

	public CapabilityNode(CapabilityInstance ci, ResourceNode parent) {
		super(ci);
		this.parent = parent;
	}

	/**
	 * @return the content
	 */
	@Override
	public CapabilityInstance getContent() {
		return (CapabilityInstance) super.getContent();
	}

	/**
	 * @param content
	 *            the content to set
	 * @throws IllegalArgumentException
	 *             if given content is not a CapabilityInstance
	 */
	@Override
	public void setContent(ApplicationInstance content) {
		if (!(content instanceof CapabilityInstance))
			throw new IllegalArgumentException("Expected CapabilityInstance");

		super.setContent(content);
	}

	/**
	 * @return the parent
	 */
	public ResourceNode getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(ResourceNode parent) {
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append(" [");
		
//		sb.append("parent=").append(parent);
		sb.append(getContent());
		
		sb.append("]");
		return sb.toString();
	}
}
