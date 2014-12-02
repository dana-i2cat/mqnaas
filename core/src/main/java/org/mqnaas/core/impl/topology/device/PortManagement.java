package org.mqnaas.core.impl.topology.device;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.topology.device.IPortManagement;

/**
 * Implementation of the {@link IPortManagement} capability using a {@link CopyOnWriteArrayList}.
 * 
 * @author Georg Mansky-Kummert
 */
public class PortManagement implements IPortManagement {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof DeviceResource;
	}
	
	private List<PortResource> ports;

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
