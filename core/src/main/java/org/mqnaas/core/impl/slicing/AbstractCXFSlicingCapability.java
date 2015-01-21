package org.mqnaas.core.impl.slicing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.endpoint.Server;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.core.api.slicing.SlicingException;
import org.mqnaas.core.impl.RootResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract implementation of the {@link ISlicingCapability} using CXF servers.
 * 
 * Based on the definition of the endpoint and the creation of the server defined in a sub-class the rest of the logic resides in this implementation.
 * 
 * @author Georg Mansky-Kummert
 */
public abstract class AbstractCXFSlicingCapability implements ISlicingCapability {

	private static final Logger				log	= LoggerFactory.getLogger(AbstractCXFSlicingCapability.class.getName());

	@Resource
	protected IRootResource					resource;

	protected Map<IRootResource, Server>	proxiesEndpoints;

	protected Collection<IResource>			virtualResources;

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing SlicingCapability for resource " + resource.getId());

		proxiesEndpoints = new HashMap<IRootResource, Server>();
		virtualResources = new ArrayList<IResource>();

		log.info("Initialized SlicingCapability for resource " + resource.getId());
	}

	@Override
	public void deactivate() {
		log.info("Removing SlicingCapability for resource " + resource.getId());

		log.debug("Unpublishing all virtual resources endpoints of resource " + resource.getId());
		for (IRootResource iRootResource : proxiesEndpoints.keySet()) {

			log.trace("Unpublishing endpoint " + proxiesEndpoints.get(iRootResource).getEndpoint());
			proxiesEndpoints.get(iRootResource).stop();
			log.trace("Endpoint " + proxiesEndpoints.get(iRootResource).getEndpoint() + " unpublished.");
		}

		proxiesEndpoints.clear();
		virtualResources.clear();

		log.info("Removed SlicingCapability for resource " + resource.getId());
	}

	/**
	 * Returns the endpoint for the virtual resource to be created using the given <code>slice</code> definition.
	 * 
	 * @param slice
	 *            the slice for which the endpoint has to be created
	 * @return the newly created endpoint
	 * @throws SlicingException
	 *             when the creation of the endpoint failed
	 */
	protected abstract Endpoint getEndpoint(IResource slice) throws SlicingException;

	/**
	 * 
	 * @param endpoint
	 * @return
	 */
	protected abstract Server createServer(Endpoint endpoint);

	@Override
	// TODO current implementation does nothing with the slice. In the future, it has to create the new resource based on the slice information.
	public IResource createSlice(IResource slice) throws SlicingException {
		log.info("Virtual resource creation request received in resource " + resource.getId());

		if (slice == null)
			throw new NullPointerException("Slice resource to create can not be null.");

		if (!(slice instanceof SliceResource))
			throw new IllegalArgumentException("Resource must be of type " + SliceResource.class.getName() + ", but is of type + " + slice.getClass()
					.getName());

		IRootResource createdResource = null;

		Endpoint endpoint = getEndpoint(slice);

		Server proxy = createServer(endpoint);
		log.trace("Publishing virtual resource proxy endpoint in url " + endpoint);
		proxy.start();

		try {
			List<Endpoint> endpoints = new ArrayList<Endpoint>();
			endpoints.add(endpoint);

			createdResource = new RootResource(RootResourceDescriptor.create(resource.getDescriptor().getSpecification().clone(), endpoints));
			createdResource.getDescriptor().getSpecification().setModel("virtual");
		} catch (Exception e) {
			log.error("Error creating virtual resource.", e);
			proxy.stop();
			throw new SlicingException(e);
		}

		virtualResources.add(createdResource);
		proxiesEndpoints.put(createdResource, proxy);

		log.info("Virtual resource created with id " + createdResource.getId());

		return createdResource;
	}

	@Override
	public void removeSlice(IResource rootResource) throws SlicingException {
		log.info("Removing virtual resource " + rootResource.getId());

		if (!virtualResources.contains(rootResource))
			throw new SlicingException(
					"Could not remove virtual resource " + rootResource + ": Resource does not exist or is not managed by this capability.");

		virtualResources.remove(rootResource);

		if (proxiesEndpoints.containsKey(rootResource)) {
			log.debug("Destroying virtual resource " + rootResource.getId() + " + proxy endpoint.");
			proxiesEndpoints.get(rootResource).stop();
			proxiesEndpoints.remove(rootResource);
			log.trace("Endpoint " + proxiesEndpoints.get(rootResource.getId()) + " unpublished.");
		}
		else
			log.warn("Virtual resource " + rootResource.getId() + " didn't have any active proxy.");

		log.info("Removed virtual resource " + rootResource.getId());
	}

	@Override
	public Collection<IResource> getSlices() {
		log.info("Getting virtual devices of resource " + resource.getId());
		return virtualResources;
	}

}
