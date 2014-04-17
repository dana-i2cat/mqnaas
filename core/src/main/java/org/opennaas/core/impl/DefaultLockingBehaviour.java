package org.opennaas.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.opennaas.core.api.ExecutionContext;
import org.opennaas.core.api.ILockingBehaviour;
import org.opennaas.core.api.IRootResource;

public class DefaultLockingBehaviour implements ILockingBehaviour {

	private Map<IRootResource, ExecutionContext> locked = new HashMap<IRootResource, ExecutionContext>();

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
		
		if ( isLocked(executionContext, resource) ) {
			locked.remove(resource);
			unlock = true;
		}
		
		return unlock;
	}

}
