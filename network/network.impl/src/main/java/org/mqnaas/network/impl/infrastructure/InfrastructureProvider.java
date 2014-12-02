package org.mqnaas.network.impl.infrastructure;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.infrastructure.IInfrastructureProvider;
import org.mqnaas.network.impl.RequestResource;

/**
 * Implementation of the {@link IInfrastructureProvider} capability that binds to
 * {@link Type#NETWORK}s and {@link RequestResource}s.
 * 
 * On activation the infrastructure resource is created and bound.
 * 
 * @author Georg Mansky-Kummert
 */

public class InfrastructureProvider implements IInfrastructureProvider {

	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Type.NETWORK;
	}
	
	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}
	
	@DependingOn
	private IResourceManagementListener resourceManagementListener; 
	
	private IResource infrastructure;
	
	@Override
	public IResource getInfrastructure() {
		return infrastructure;
	}

	@Override
	public void activate() {
		// TODO: persistence
		infrastructure = new InfrastructureResource();
		
		// Add resource manually to the platform
		resourceManagementListener.resourceAdded(infrastructure, this);
	}

	@Override
	public void deactivate() {
		// TODO: persistence
		
		// Remove resource manually from the platform
		resourceManagementListener.resourceRemoved(infrastructure, this);
	}

}
