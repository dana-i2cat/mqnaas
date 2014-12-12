package org.mqnaas.network.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.i2cat.dana.mqnaas.capability.reservation.IReservationCapability;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.network.api.exceptions.NetworkCreationException;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.impl.request.Request;

/**
 * Manages network resources and provides services to create and release new networks based on network request.
 * 
 * @author Georg Mansky-Kummert
 */
public class NetworkManagement implements IRequestBasedNetworkManagement {

	public static boolean isSupporting(IRootResource rootResource) {
		return rootResource.getDescriptor().getSpecification().getType() == Type.NETWORK;
	}

	@DependingOn
	IRootResourceAdministration			rootResourceAdministration;

	@DependingOn
	IRootResourceProvider				rootResourceProvider;

	@DependingOn
	private IResourceManagementListener	resourceManagementListener;

	@DependingOn
	IServiceProvider					serviceProvider;

	@DependingOn
	IReservationCapability				reservationCapability;

	private List<IRootResource>			networks;

	@Override
	public IRootResource createNetwork(IResource requestResource) throws NetworkCreationException {

		IRootResource networkResource = null;
		List<IRootResource> resourcesToReserve = new ArrayList<IRootResource>();

		try {
			networkResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));

			// Manual bind, so that capabilities exist
			resourceManagementListener.resourceAdded(networkResource, this, IRequestBasedNetworkManagement.class);

			Request request = new Request(requestResource, serviceProvider);

			List<IRootResource> networkRootResources = request.getRootResources();
			for (IRootResource netRootResource : networkRootResources) {

				// wrapp resource
				NetworkSubResource resource = new NetworkSubResource(netRootResource, serviceProvider);

				// get the physical resource (device is mapped to this resource)
				IRootResource phyRootResource = (IRootResource) request.getMappedDevice(netRootResource);

				// create slice if it's a sliceable resource
				ISlicingCapability slicingCapability = resource.getSlicingCapability();
				if (slicingCapability != null) {

					Collection<IResource> slices = slicingCapability.getSlices();
					ISlicingCapability phyResourceSliceCapab = serviceProvider.getCapability(phyRootResource, ISlicingCapability.class);

					// for each slice, create slice and add new sliced resource to network
					for (IResource slice : slices) {
						IResource newResource = phyResourceSliceCapab.createSlice(slice);
						resourcesToReserve.add((IRootResource) newResource);
					}

				}

				// If the resource is a subnetwork, create request and delegate to it
				IRequestBasedNetworkManagement subnetManagementCapability = resource.getRequestBasedNetworkManagementCapability();
				if (subnetManagementCapability != null) {
					IResource subnetRequest = new RequestResource();
					// TODO what to fill?
					IRootResource subnetPhyResource = subnetManagementCapability.createNetwork(subnetRequest);
					Network subnetwork = new Network(subnetPhyResource, serviceProvider);
					List<IRootResource> subnetResources = subnetwork.getResources();
					resourcesToReserve.addAll(subnetResources);

				}

				// reserve devices
			}
			// TODO links

			// try {
			//
			// TopologyWrapper topology = network.getTopology();
			//
			// InfrastructureWrapper infrastructure = network.getInfrastructure();
			//
			// // Now, add all resources to the network, to the topology and to the
			// // infrastructure
			//
			// RequestWrapper request = new RequestWrapper(requestResource);
			// for (DeviceWrapper requestedDevice : request.getTopology()
			// .getDevices()) {
			//
			// // Create the device in the network's topology
			// DeviceWrapper device = topology.createDevice();
			//
			// // Obtain the mapping from the request...
			// IResource requestedResource = request.getInfrastructure()
			// .getDeviceMapping(requestedDevice);
			//
			// // Get the slice, if available...
			// IResource slice = null;
			//
			// IResource resource;
			//
			// if (slice != null) {
			// // Slice definition available. Create a slice of the
			// // corresponding resource
			// ISlicingCapability slicingCapability = serviceProvider
			// .getCapability(requestedResource,
			// ISlicingCapability.class);
			//
			// resource = slicingCapability.createSlice(slice);
			// } else {
			// // use the complete resource
			// // TODO Manage already assigned resources...
			// resource = requestedResource;
			// }
			//
			// network.addResource(resource);
			//
			// infrastructure.defineDeviceMapping(device, resource);
			// }

			// } catch (CapabilityNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			// Add the network to the internal list
			networks.add(networkResource);

		} catch (InstantiationException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		} catch (IllegalAccessException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		} catch (CapabilityNotFoundException c) {
			throw new NetworkCreationException("Network creation failed.", c);
		}

		return networkResource;
	}

	@Override
	public void releaseNetwork(IRootResource network) {
		// I) Release all resource of the network
		resourceManagementListener.resourceRemoved(network, this, IRequestBasedNetworkManagement.class);

		// II) Delete the network
	}

	private class SliceWrapper {

		private IResource	slice;

		public SliceWrapper(IResource slice) {
			this.slice = slice;
		}

	}

	@Override
	public Collection<IRootResource> getNetworks() {
		return networks;
	}

	@Override
	public void activate() {
		networks = new CopyOnWriteArrayList<IRootResource>();
	}

	@Override
	public void deactivate() {
	}

}
