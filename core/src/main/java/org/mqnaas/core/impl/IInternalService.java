package org.mqnaas.core.impl;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IService;

public interface IInternalService extends IService {

	/**
	 * <p>
	 * Sets the {@link IResource} coupled to this service.
	 * </p>
	 * Due to initialization issues, the internal service representation has to provide the possibility to set the coupled Resource.
	 * 
	 * @param resource
	 *            The resource to be set
	 */
	public void setResource(IResource resource);

	/**
	 * Execute the service with the given parameters
	 * 
	 * @param parameters
	 *            The parameters used when executing the service
	 * @return The service execution result
	 */
	public Object execute(Object[] parameters);

}
