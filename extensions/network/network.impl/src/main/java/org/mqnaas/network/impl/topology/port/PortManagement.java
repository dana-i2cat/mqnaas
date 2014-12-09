package org.mqnaas.network.impl.topology.port;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.network.api.topology.port.IPortManagement;

/**
 * Implementation of the {@link IPortManagement} capability using a {@link CopyOnWriteArrayList}. This implementation should be bound to all
 * IRootResources except core and network ones.
 * 
 * @author Georg Mansky-Kummert
 */
public class PortManagement implements IPortManagement {

	public static boolean isSupporting(IRootResource resource) {
		return (!resource.getDescriptor().getSpecification().getType().equals(Type.CORE) && !resource.getDescriptor().getSpecification().getType()
				.equals(Type.NETWORK));

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
