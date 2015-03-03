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

import java.util.HashMap;
import java.util.Map;

import org.mqnaas.core.api.ExecutionContext;
import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IRootResource;

public class DefaultLockingBehaviour implements ILockingBehaviour {

	private Map<IRootResource, ExecutionContext>	locked	= new HashMap<IRootResource, ExecutionContext>();

	@Override
	public boolean lock(ExecutionContext executionContext, IRootResource resource) {

		if (!locked.containsKey(resource)) {
			locked.put(resource, executionContext);
		}

		return isLocked(executionContext, resource);
	}

	@Override
	public boolean isLocked(ExecutionContext executionContext, IRootResource resource) {
		return locked.get(resource).equals(executionContext);
	}

	@Override
	public boolean unlock(ExecutionContext executionContext, IRootResource resource) {
		boolean unlock = false;

		if (isLocked(executionContext, resource)) {
			locked.remove(resource);
			unlock = true;
		}

		return unlock;
	}

}
