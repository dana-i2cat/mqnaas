package org.mqnaas.core.api;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

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
@Path("/mqnaas/resources")
public interface IRootResourceManagement extends ICapability {

	/**
	 * <p>
	 * Adds the given {@link IResource} to the resources managed by the platform.
	 * </p>
	 * <p>
	 * The service {@link IResourceManagementListener#resourceAdded(IResource, IApplication, Class)} is notified, whenever this service was executed successfully.
	 * </p>
	 * 
	 * @param resource
	 *            The resource to be added to the platform
	 */

	@AddsResource
	@PUT
	// TODO Rethink the exceptions thrown by this service
	IRootResource createRootResource(RootResourceDescriptor descriptor) throws InstantiationException, IllegalAccessException;

	/**
	 * <p>
	 * Remove the given {@link IResource} from the resources managed by the platform.
	 * </p>
	 * 
	 * <p>
	 * The service {@link IResourceManagementListener#resourceRemoved(IResource, IApplication, Class)} is notified whenever this service was executed successfully.
	 * 
	 * @param resource
	 *            The resource to be removed from the platform
	 */

	@RemovesResource
	@DELETE
	void removeRootResource(IRootResource resource);

	/**
	 * Returns all {@link IResource} currently managed by the platform.
	 * 
	 * @return The currently managed resources
	 */
	@GET
	@ListsResources
	List<IRootResource> getRootResources();

	@ListsResources
	List<IRootResource> getRootResources(Specification.Type type, String model, String version) throws ResourceNotFoundException;

	IRootResource getCore();

	/**
	 * Returns the {@link IRootResource} with id <code>id</code>
	 * 
	 * @param id
	 *            Id of the resource to be retrieved.
	 * @return The <code>IRootResource</code> with id <code>id</code>
	 * @throws ResourceNotFoundException
	 *             If there's no managed resource with such id.
	 */
	IRootResource getRootResource(String id) throws ResourceNotFoundException;

}
