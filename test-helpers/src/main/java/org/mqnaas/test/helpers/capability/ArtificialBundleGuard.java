package org.mqnaas.test.helpers.capability;

import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;

/**
 * Implementation of {@link IBundleGuard} capability allowing to send artificially generated events using
 * {@link #throwClassEntered(IClassListener, Class)} and {@link #throwClassLeft(IClassListener, Class)} methods.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ArtificialBundleGuard implements IBundleGuard {

	@Override
	public void activate() {
		// nothing to do
	}

	@Override
	public void deactivate() {
		// nothing to do
	}

	@Override
	public void registerClassListener(IClassFilter classFilter, IClassListener classListener) {
		// nothing to do
	}

	@Override
	public void unregisterClassListener(IClassListener classListener) {
		// nothing to do
	}

	/**
	 * Generate an artificial {@link IClassListener#classEntered(Class)} event.
	 * 
	 * @param classListener
	 *            {@link ClassListener} to send the event
	 * @param clazz
	 *            event {@link Class}
	 */
	public void throwClassEntered(IClassListener classListener, Class<?> clazz) {
		classListener.classEntered(clazz);
	}

	/**
	 * Generate an artificial {@link IClassListener#classLeft(Class)} event.
	 * 
	 * @param classListener
	 *            {@link ClassListener} to send the event
	 * @param clazz
	 *            event {@link Class}
	 */
	public void throwClassLeft(IClassListener classListener, Class<?> clazz) {
		classListener.classLeft(clazz);
	}

}
