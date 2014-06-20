package org.mqnaas.core.impl.dummy;

import org.mqnaas.bundletree.IBundleGuard;
import org.mqnaas.bundletree.IClassFilter;
import org.mqnaas.bundletree.IClassListener;

/**
 * Dummy BundleGuard used only in unit tests
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class DummyBundleGuard implements IBundleGuard {

	@Override
	public void registerClassListener(IClassFilter classFilter, IClassListener classListener) {
		// do nothing
	}

	@Override
	public void unregisterClassListener(IClassListener classListener) {
		// do nothing
	}

}
