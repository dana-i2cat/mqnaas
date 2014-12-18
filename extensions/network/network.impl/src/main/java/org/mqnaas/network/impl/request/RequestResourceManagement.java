package org.mqnaas.network.impl.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.network.api.request.IRequestResourceManagement;
import org.mqnaas.network.impl.RequestRootResource;

/**
 * Implementation of the {@link IRequestResourceManagement} capability using a {@link CopyOnWriteArrayList}. This implementation is bound to the
 * {@link RequestResource} itself and to all {@link RequestRootResource}s of type network.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestResourceManagement implements IRequestResourceManagement {

	private List<RequestRootResource>	resources;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource || (resource instanceof RequestRootResource && ((RequestRootResource) resource).getType() == Type.NETWORK);
	}

	@Override
	public IResource createResource(Type type) {
		RequestRootResource resource = new RequestRootResource(type);
		resources.add(resource);
		return resource;
	}

	@Override
	public void removeResource(IResource resource) {
		resources.remove(resource);
	}

	@Override
	public List<IResource> getResources() {
		return new ArrayList<IResource>(resources);
	}
	
	@Override
	public void activate() {
		resources = new CopyOnWriteArrayList<RequestRootResource>();
	}

	@Override
	public void deactivate() {
	}

}
