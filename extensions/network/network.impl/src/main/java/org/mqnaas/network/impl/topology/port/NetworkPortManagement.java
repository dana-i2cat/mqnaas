package org.mqnaas.network.impl.topology.port;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;
import org.mqnaas.network.impl.RequestRootResource;

/**
 * Implementation of the {@link INetworkPortManagement} capability using a {@link CopyOnWriteArrayList}. This implementation is bound to networks.
 * 
 * @author Georg Mansky-Kummert
 */
public class NetworkPortManagement implements INetworkPortManagement {

	public static boolean isSupporting(IRootResource resource) {
		Type type = resource.getDescriptor().getSpecification().getType();

		return type.equals(Type.NETWORK);
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
	public void addPort(IResource port) {
		if (!(port instanceof PortResource))
			throw new IllegalArgumentException("Given resource is not a port!");
		ports.add((PortResource) port);
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
