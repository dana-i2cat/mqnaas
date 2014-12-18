package org.mqnaas.network.impl.topology.port;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.network.api.topology.port.IPortManagement;
import org.mqnaas.network.impl.request.RequestRootResource;

/**
 * Implementation of the {@link IPortManagement} capability using a {@link CopyOnWriteArrayList}. This implementation is bound to all
 * {@link IRootResource}s except core and network ones and, in addition, to all {@link RequestRootResource}s.
 * 
 * @author Georg Mansky-Kummert
 */
public class PortManagement implements IPortManagement {

	public static boolean isSupporting(IRootResource resource) {
		Type type = resource.getDescriptor().getSpecification().getType();

		return (!type.equals(Type.CORE) && !type.equals(Type.NETWORK));
	}

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestRootResource;
	}

	private List<PortResource>	ports;

	@Override
	public void activate() {
		ports = new CopyOnWriteArrayList<PortResource>();
	}

	@Override
	public void deactivate() {
	}

	@Override
	public IResource createPort() {
		PortResource port = new PortResource();
		ports.add(port);
		return port;
	}

	@Override
	public void removePort(IResource port) {
		ports.remove(port);
	}

	@Override
	public List<IResource> getPorts() {
		return new ArrayList<IResource>(ports);
	}

}
