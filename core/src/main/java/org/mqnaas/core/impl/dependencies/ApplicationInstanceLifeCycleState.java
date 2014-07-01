package org.mqnaas.core.impl.dependencies;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public enum ApplicationInstanceLifeCycleState {
	INSTANTIATED,
	RESOLVED,
	ACTIVATING,
	ACTIVE,
	DEACTIVATING,
	DESTROYING,
	ERROR;

}
