package org.mqnaas.core.api.topology.device;

import java.util.List;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;


/**
 * Manages a topology's devices. To be bound to a topology resource.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IDeviceManagement extends ICapability {
	
	@AddsResource
	IResource createDevice();
	
	@RemovesResource
	void removeDevice(IResource device);
	
	@ListsResources
	List<IResource> getDevices();

}
