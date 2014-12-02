package org.mqnaas.core.impl.topology;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.impl.network.RequestResource;
import org.mqnaas.network.api.topology.ITopologyProvider;

/**
 * Implementation of the {@link ITopologyProvider} capability that binds to
 * {@link Type#NETWORK}s and {@link RequestResource}s.
 * 
 * On activation the topology resource is created and bound.
 * 
 * @author Georg Mansky-Kummert
 */
public class TopologyProvider implements ITopologyProvider {

	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Type.NETWORK;
	}

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	@DependingOn
	private IResourceManagementListener resourceManagementListener;

	private IResource topology;

	@Override
	public IResource getTopology() {
		return topology;
	}

	@Override
	public void activate() {
		// TODO: persistence (only create once)
		topology = new TopologyResource();

		// Add resource manually to the platform
		resourceManagementListener.resourceAdded(topology, this);
	}

	@Override
	public void deactivate() {
		// TODO: persistence

		// Remove resource manually from the platform
		resourceManagementListener.resourceRemoved(topology, this);
	}

}
