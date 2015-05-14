package org.mqnaas.test.helpers.capability.sample;

/*
 * #%L
 * MQNaaS :: MQNaaS Test Helpers
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

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.ITransactionBehavior;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;

/**
 * Resource A to be used in tests.
 * 
 * @author Julio Carlos Barrera (i2CAT Foundation)
 *
 */
public class ResourceA implements IRootResource {

	public static final Specification.Type	RESOURCE_A_TYPE		= Specification.Type.OTHER;
	public static final String				RESOURCE_A_MODEL	= "resourceA";

	private String							id;

	private static AtomicInteger			ID_COUNTER			= new AtomicInteger();

	public ResourceA() {
		id = "unit-" + ID_COUNTER.incrementAndGet();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public RootResourceDescriptor getDescriptor() {
		return null;
	}

	@Override
	public ITransactionBehavior getTransactionBehaviour() {
		return null;
	}

	@Override
	public ILockingBehaviour getLockingBehaviour() {
		return null;
	}

}
