package org.mqnaas.core.impl.topology.device;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.network.api.topology.device.IDeviceAdministration;
import org.mqnaas.network.api.topology.device.IPortManagement;

/**
 * A device administration capability bound to a {@link DeviceResource}. Manages
 * ports and modifies the device's {@link Type}.
 * 
 * @author Georg Mansky-Kummert
 */
public class DeviceAdministration implements IPortManagement,
		IDeviceAdministration {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof DeviceResource;
	}

	private Type type;

	private List<PortResource> ports;

	@Override
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public Type getType() {
		return type;
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

	@Override
	public void activate() {
		ports = new CopyOnWriteArrayList<PortResource>();
	}

	@Override
	public void deactivate() {
	}

}
