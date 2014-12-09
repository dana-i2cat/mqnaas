package org.mqnaas.network.impl.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.network.api.request.IRequestManagement;
import org.mqnaas.network.impl.RequestResource;

/**
 * Implementation of the {@link IRequestManagement} capability using a {@link CopyOnWriteArrayList}. This implementation is bound to {@link Type#NETWORK}s.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestManagement implements IRequestManagement {

	private List<RequestResource>	requests;

	public static boolean isSupporting(IRootResource resource) {
		return (resource.getDescriptor().getSpecification().getType().equals(Type.NETWORK));
	}

	@Override
	public IResource createRequest() {
		RequestResource request = new RequestResource();
		requests.add(request);
		return request;
	}

	@Override
	public void removeRequest(IResource request) {
		requests.remove(request);
	}

	@Override
	public List<IResource> getRequests() {
		return new ArrayList<IResource>(requests);
	}

	@Override
	public void activate() {
		requests = new CopyOnWriteArrayList<RequestResource>();
	}

	@Override
	public void deactivate() {
	}

}
