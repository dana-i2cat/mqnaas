package org.mqnaas.bundletree;

import org.mqnaas.core.api.ICapability;

/**
 * Bundle Guard interface allowing to register/unregister {@link IClassListener} with a {@link IClassFilter}. It will notify {@link Class}es
 * entering/leaving system classpath through IClassListener callback methods based on IClassFilter logic.
 * 
 * @author Julio Carlos Barrera
 * 
 */
public interface IBundleGuard extends ICapability {

	/**
	 * Registers a {@link IClassListener} using a {@link IClassFilter}.
	 * 
	 * @param classFilter
	 *            IClassFilter to be used as class filtering logic
	 * @param classListener
	 *            IClassListener to be called back when necessary
	 */
	public void registerClassListener(IClassFilter classFilter, IClassListener classListener);

	/**
	 * Unregister a previously registered {@link IClassListener}. This operation removes first IClassListener found registered with given
	 * IClassFilter. For this reason it is recommended registering each IClassListener as a different instance.
	 * 
	 * @param classListener
	 *            IClassListener to be unregistered
	 */
	public void unregisterClassListener(IClassListener classListener);
}
