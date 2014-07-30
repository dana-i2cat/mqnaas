package org.mqnaas.test.helpers.capability;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.impl.ICoreModelCapability;

/**
 * {@link ICapability}'s factory that provides test implementations useful for tests.
 * 
 * @author Julio Carlos Barrera
 *
 */
public class TestCapabilitiesFactory {

	/**
	 * @see ArtificialBundleGuard
	 * 
	 */
	public static ArtificialBundleGuard getArtificialBundleGuard() {
		return new ArtificialBundleGuard();
	}

	/**
	 * @see ArtificialCoreModelCapability
	 * 
	 */
	public static ICoreModelCapability getArtificialCoreModelCapability(final IRootResource resourceToBeReturned) {
		return new ArtificialCoreModelCapability(resourceToBeReturned);
	}
}
