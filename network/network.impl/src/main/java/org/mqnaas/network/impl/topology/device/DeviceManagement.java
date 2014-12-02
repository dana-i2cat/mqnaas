package org.mqnaas.network.impl.topology.device;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.network.api.topology.device.IDeviceManagement;
import org.mqnaas.network.impl.topology.TopologyResource;

/**
 * Implementation of the {@link IDeviceManagement} capabilty using a
 * {@link CopyOnWriteArrayList}.
 * 
 * @author Georg Mansky-Kummert
 */
public class DeviceManagement implements IDeviceManagement {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof TopologyResource;
	}

	private List<DeviceResource> devices;

	@Override
	public IResource createDevice() {
		DeviceResource device = new DeviceResource();
		devices.add(device);
		return device;
	}

	@Override
	public void removeDevice(IResource device) {
		devices.remove(device);
	}

	@Override
	public List<IResource> getDevices() {
		return new ArrayList<IResource>(devices);
	}

	@Override
	public void activate() {
		devices = new CopyOnWriteArrayList<DeviceResource>();
	}

	@Override
	public void deactivate() {
	}

}
