package org.mqnaas.test.helpers.capability;

import org.mqnaas.core.api.ICoreModelCapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;

/**
 * Artificial {@link ICoreModelCapability} that always returns given {@link IRootResource} in {@link #getRootResource(IResource)} method.
 * 
 * @author Julio Carlos Barrera
 *
 */
class ArtificialCoreModelCapability implements ICoreModelCapability {

	private IRootResource	resourceToBeReturned;

	/**
	 * Constructor to be used in order to return given {@link IRootResource} when {@link #getRootResource(IResource)} method is invoked.
	 * 
	 * @param resourceToBeReturned
	 *            IRootResource to be returned
	 */
	public ArtificialCoreModelCapability(IRootResource resourceToBeReturned) {
		this.resourceToBeReturned = resourceToBeReturned;
	}

	@Override
	public void activate() {
		// do nothing
	}

	@Override
	public void deactivate() {
		// do nothing
	}

	@Override
	public IRootResource getRootResource(IResource resource) throws IllegalArgumentException {
		return resourceToBeReturned;
	}

}
