package org.mqnaas.core.api;

import java.util.List;

import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

/**
 * <p>
 * <code>IResourceManagement</code> is one of the core capabilities of MQNaaS.
 * </p>
 * 
 * <p>
 * It manages all {@link IResource}s available in the platform. These resources may either be representations of physical devices or any other unity
 * offering its services.
 * </p>
 * 
 */
public interface IRootResourceManagement extends ICapability {

	/**
	 * <p>
	 * Adds the given {@link IResource} to the resources managed by the platform.
	 * </p>
	 * <p>
	 * The service {@link IBindingManagement#resourceAdded(IResource)} is notified, whenever this service was executed successfully.
	 * </p>
	 * 
	 * @param resource
	 *            The resource to be added to the platform
	 */

	@AddsResource
	IRootResource createRootResource(RootResourceDescriptor descriptor);

	/**
	 * <p>
	 * Remove the given {@link IResource} from the resources managed by the platform.
	 * </p>
	 * 
	 * <p>
	 * The service {@link IBindingManagement#resourceRemoved(IResource)} is notified whenever this service was executed successfully.
	 * 
	 * @param resource
	 *            The resource to be removed from the platform
	 */

	@RemovesResource
	void removeRootResource(IRootResource resource);

	/**
	 * Returns all {@link IResource} currently managed by the platform.
	 * 
	 * @return The currently managed resources
	 */

	@ListsResources
	List<IRootResource> getRootResources();

	@ListsResources
	List<IRootResource> getRootResources(Specification.Type type, String model, String version);

	IRootResource getCore();

}
