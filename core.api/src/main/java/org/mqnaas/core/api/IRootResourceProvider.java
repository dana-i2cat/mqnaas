package org.mqnaas.core.api;

import java.util.List;

import javax.ws.rs.GET;

import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

/**
 * This capability provides access to the {@link IRootResource}s managed by the core or a network. Is bound to the core, and to physical and virtual
 * networks as well as testbeds like NITOS. When used with static networks, e.g. that do not have a administration capability, it's internal state has
 * to initialized from an external logic.
 * 
 * @author Georg Mansky-Kummert (i2CAT)
 * @author Adrián Roselló Rey (i2CAT)
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public interface IRootResourceProvider extends ICapability {

	/**
	 * Returns all {@link IRootResource}s currently managed by this capability.
	 * 
	 * @return The list of {@link IRootResource}s managed by the capability.
	 */
	@GET
	@ListsResources
	List<IRootResource> getRootResources();

	/**
	 * Returns the subset of {@link IRootResource}s managed by this capability matching a specific {@link Specification.Type}, model and version.
	 * 
	 * @param type
	 *            Resource type.
	 * @param model
	 *            Resource model.
	 * @param version
	 *            Resource version
	 * @return
	 * @throws ResourceNotFoundException
	 */
	@ListsResources
	List<IRootResource> getRootResources(Specification.Type type, String model, String version) throws ResourceNotFoundException;

	IRootResource getCore();

	/**
	 * Returns a specific {@link IRootResource} identified by the given id.
	 * 
	 * @param id
	 *            Id of the IRootResource.
	 * @return IRootResource identified by given <code>id</code>
	 * @throws ResourceNotFoundException
	 *             If there's no RootResource managed by this capability instance which such id.
	 */
	IRootResource getRootResource(String id) throws ResourceNotFoundException;

}