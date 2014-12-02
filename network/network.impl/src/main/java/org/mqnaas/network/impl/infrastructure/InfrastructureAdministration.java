package org.mqnaas.network.impl.infrastructure;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mqnaas.core.api.IResource;
import org.mqnaas.network.api.infrastructure.IInfrastructureAdministration;
import org.mqnaas.network.impl.topology.device.DeviceResource;

/**
 * Implementation of the {@link IInfrastructureAdministration} capability backed by two {@link ConcurrentHashMap}s.
 *  
 * @author Georg Mansky-Kummert
 */
public class InfrastructureAdministration implements
		IInfrastructureAdministration {

	public static boolean isSupporting(IResource resource) {
		return resource instanceof InfrastructureResource;
	}

	private Map<IResource, IResource> deviceMapping;

	private Map<IResource, IResource> linkMapping;

	@Override
	public void defineDeviceMapping(IResource resource, IResource rootResource) {
		if (!(resource instanceof DeviceResource))
			throw new IllegalArgumentException("Resource must be a device.");
		
		deviceMapping.put(resource, rootResource);
	}

	@Override
	public void removeDeviceMapping(IResource resource) {
		deviceMapping.remove(resource);
	}

	@Override
	public IResource getDeviceMapping(IResource resource) {
		return deviceMapping.get(resource);
	}

	@Override
	public Collection<IResource> getMappedDevices() {
		return deviceMapping.keySet();
	}
	
	@Override
	public void defineLinkMapping(IResource link, IResource resource) {
		linkMapping.put(link, resource);
	}

	@Override
	public void removeLinkMapping(IResource resource) {
		linkMapping.remove(resource);
	}

	@Override
	public IResource getLinkMapping(IResource resource) {
		return linkMapping.get(resource);
	}
	
	@Override
	public Collection<IResource> getMappedLinks() {
		return linkMapping.keySet();
	}

	@Override
	public void activate() {
		deviceMapping = new ConcurrentHashMap<IResource, IResource>();
		linkMapping = new ConcurrentHashMap<IResource, IResource>();
	}

	@Override
	public void deactivate() {
	}

}
