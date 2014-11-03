package org.mqnaas.core.api;


/**
 * This {@link ICapability} groups all the operations that can be requested to core implementation.
 * 
 * @author Julio Carlos Barrera
 *
 */
public interface ICoreModelCapability extends ICapability {

	/**
	 * Retrieves the {@link IRootResource} that corresponds to a given {@link IResource} in core model.
	 * 
	 * @param resource
	 *            IResource from where it is necessary to look for
	 * @return IRootResource corresponding to given resource
	 * @throws IllegalArgumentException
	 *             if given resource is not present in the model
	 */
	public IRootResource getRootResource(IResource resource) throws IllegalArgumentException;

}
