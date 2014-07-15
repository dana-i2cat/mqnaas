package org.mqnaas.core.impl.dependencies;

import org.mqnaas.core.impl.ApplicationInstance;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public interface ApplicationInstanceLifeCycleStateListener {

	/**
	 * Called when an ApplicationInstance is instantiated
	 * 
	 * @param application
	 */
	public void instantiated(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is resolved (its last dependency is assigned)
	 * 
	 * @param application
	 */
	public void resolved(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is activated
	 * 
	 * @param application
	 */
	public void activated(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is deactivated
	 * 
	 * @param application
	 */
	public void deactivated(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is unresolved (had all dependencies assigned and it loses one)
	 * 
	 * @param application
	 */
	public void unresolved(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is destroyed
	 * 
	 * @param application
	 */
	public void destroyed(ApplicationInstance application);

}
