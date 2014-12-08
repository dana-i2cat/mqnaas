package org.mqnaas.network.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.network.api.infrastructure.IInfrastructureAdministration;
import org.mqnaas.network.api.infrastructure.IInfrastructureProvider;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.api.topology.ITopologyProvider;
import org.mqnaas.network.api.topology.device.IDeviceAdministration;
import org.mqnaas.network.api.topology.device.IDeviceManagement;
import org.mqnaas.network.api.topology.link.ILinkAdministration;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.IPortAdministration;
import org.mqnaas.network.api.topology.port.IPortManagement;

/**
 * Manages network resources and provides services to create and release new networks based on network request.
 * 
 * @author Georg Mansky-Kummert
 */
public class NetworkManagement implements IRequestBasedNetworkManagement {

	public static boolean isSupporting(IRootResource rootResource) {
		return rootResource.getDescriptor().getSpecification().getType() == Type.CORE;
	}

	@DependingOn
	IRootResourceAdministration			rootResourceAdministration;

	@DependingOn
	IRootResourceProvider				rootResourceProvider;

	@DependingOn
	private IResourceManagementListener	resourceManagementListener;

	@DependingOn
	private IServiceProvider			serviceProvider;

	private List<IRootResource>			networks;

	@Override
	public IRootResource createNetwork(IResource requestResource) {

		IRootResource networkResource = null; // TODO No way to create a root
												// resource;
		networks.add(networkResource);

		NetworkWrapper network = new NetworkWrapper(networkResource);

		// Manual bind, so that capabilities exist
		resourceManagementListener.resourceAdded(networkResource, this, IRequestBasedNetworkManagement.class);

		try {
			TopologyWrapper topology = network.getTopology();

			InfrastructureWrapper infrastructure = network.getInfrastructure();

			// Now, add all resources to the network, to the topology and to the
			// infrastructure

			RequestWrapper request = new RequestWrapper(requestResource);
			for (DeviceWrapper requestedDevice : request.getTopology()
					.getDevices()) {

				// Create the device in the network's topology
				DeviceWrapper device = topology.createDevice();

				// Obtain the mapping from the request...
				IResource requestedResource = request.getInfrastructure()
						.getDeviceMapping(requestedDevice);

				// Get the slice, if available...
				IResource slice = null;

				IResource resource;

				if (slice != null) {
					// Slice definition available. Create a slice of the
					// corresponding resource
					ISlicingCapability slicingCapability = serviceProvider
							.getCapability(requestedResource,
									ISlicingCapability.class);

					resource = slicingCapability.createSlice(slice);
				} else {
					// use the complete resource
					// TODO Manage already assigned resources...
					resource = requestedResource;
				}

				network.addResource(resource);

				infrastructure.defineDeviceMapping(device, resource);
			}

		} catch (CapabilityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return networkResource;
	}

	@Override
	public void releaseNetwork(IRootResource network) {
		// I) Release all resource of the network
		resourceManagementListener.resourceRemoved(network, this, IRequestBasedNetworkManagement.class);

		// II) Delete the network
	}

	private class RequestWrapper {

		private IResource				request;

		private TopologyWrapper			topologyWrapper;

		private InfrastructureWrapper	infrastructureWrapper;

		public RequestWrapper(IResource request) {
			this.request = request;
		}

		public InfrastructureWrapper getInfrastructure()
				throws CapabilityNotFoundException {
			if (infrastructureWrapper == null) {
				infrastructureWrapper = new InfrastructureWrapper(
						serviceProvider.getCapability(request,
								IInfrastructureProvider.class)
								.getInfrastructure());
			}
			return infrastructureWrapper;
		}

		public TopologyWrapper getTopology() throws CapabilityNotFoundException {
			if (topologyWrapper == null) {
				topologyWrapper = new TopologyWrapper(serviceProvider
						.getCapability(request, ITopologyProvider.class)
						.getTopology());
			}
			return topologyWrapper;
		}

	}

	private class NetworkWrapper {

		private IResource				network;

		private TopologyWrapper			topologyWrapper;

		private InfrastructureWrapper	infrastructureWrapper;

		public NetworkWrapper(IResource network) {
			this.network = network;
		}

		public void addResource(IResource resource)
				throws CapabilityNotFoundException {
			getResourceManagementListener().resourceAdded(resource,
					getRootResourceAdministration(), IRootResourceAdministration.class);
		}

		private IResourceManagementListener getResourceManagementListener()
				throws CapabilityNotFoundException {
			return serviceProvider.getCapability(network,
					IResourceManagementListener.class);
		}

		private IRootResourceAdministration getRootResourceAdministration()
				throws CapabilityNotFoundException {
			return serviceProvider.getCapability(network,
					IRootResourceAdministration.class);
		}

		public TopologyWrapper getTopology() throws CapabilityNotFoundException {
			if (topologyWrapper == null) {
				topologyWrapper = new TopologyWrapper(serviceProvider
						.getCapability(network, ITopologyProvider.class)
						.getTopology());
			}
			return topologyWrapper;
		}

		public InfrastructureWrapper getInfrastructure()
				throws CapabilityNotFoundException {
			if (infrastructureWrapper == null) {
				infrastructureWrapper = new InfrastructureWrapper(
						serviceProvider.getCapability(network,
								IInfrastructureProvider.class)
								.getInfrastructure());
			}
			return infrastructureWrapper;
		}

	}

	private class TopologyWrapper {

		private IResource	topology;

		public TopologyWrapper(IResource topology) {
			this.topology = topology;
		}

		private ILinkManagement getLinkManagement()
				throws CapabilityNotFoundException {
			return serviceProvider.getCapability(topology,
					ILinkManagement.class);
		}

		public LinkWrapper createLink() throws CapabilityNotFoundException {
			return new LinkWrapper(getLinkManagement().createLink());
		}

		private IDeviceManagement getDeviceManagement()
				throws CapabilityNotFoundException {
			return serviceProvider.getCapability(topology,
					IDeviceManagement.class);
		}

		public DeviceWrapper createDevice() throws CapabilityNotFoundException {
			return new DeviceWrapper(getDeviceManagement().createDevice());
		}

		public Collection<DeviceWrapper> getDevices()
				throws CapabilityNotFoundException {
			List<DeviceWrapper> devices = new ArrayList<DeviceWrapper>();

			for (IResource device : getDeviceManagement().getDevices()) {
				devices.add(new DeviceWrapper(device));
			}

			return devices;
		}

	}

	private class InfrastructureWrapper {

		private IResource	infrastructure;

		public InfrastructureWrapper(IResource infrastructure) {
			this.infrastructure = infrastructure;
		}

		public void defineDeviceMapping(DeviceWrapper device, IResource resource)
				throws CapabilityNotFoundException {
			getAdministration().defineDeviceMapping(device.getResource(),
					resource);
		}

		public IResource getDeviceMapping(DeviceWrapper device)
				throws CapabilityNotFoundException {
			return getAdministration().getDeviceMapping(device.getResource());
		}

		private IInfrastructureAdministration getAdministration()
				throws CapabilityNotFoundException {
			return serviceProvider.getCapability(infrastructure,
					IInfrastructureAdministration.class);
		}

	}

	private class DeviceWrapper {

		private IResource	device;

		public DeviceWrapper(IResource device) {
			this.device = device;
		}

		public IResource getResource() {
			return device;
		}

		private IDeviceAdministration getDeviceAdministration()
				throws CapabilityNotFoundException {
			return serviceProvider.getCapability(device,
					IDeviceAdministration.class);
		}

		public Type getType() throws CapabilityNotFoundException {
			return getDeviceAdministration().getType();
		}

		public PortWrapper createPort() throws CapabilityNotFoundException {
			return new PortWrapper(getPortManagement().createPort());
		}

		private IPortManagement getPortManagement()
				throws CapabilityNotFoundException {
			return serviceProvider.getCapability(device, IPortManagement.class);
		}

	}

	private class PortWrapper {

		private IResource	port;

		public PortWrapper(IResource port) {
			this.port = port;
		}

		private IPortAdministration getPortAdministration()
				throws CapabilityNotFoundException {
			return serviceProvider.getCapability(port,
					IPortAdministration.class);
		}

		public void setName(String name) throws CapabilityNotFoundException {
			getPortAdministration().setName(name);
		}

		public IResource getResource() {
			return port;
		}

	}

	private class LinkWrapper {

		private IResource	link;

		public LinkWrapper(IResource link) {
			this.link = link;
		}

		private ILinkAdministration getLinkAdministration()
				throws CapabilityNotFoundException {
			return serviceProvider.getCapability(link,
					ILinkAdministration.class);
		}

		public void setDestPort(PortWrapper port)
				throws CapabilityNotFoundException {
			getLinkAdministration().setSrcPort(port.getResource());
		}

		public void setSrcPort(PortWrapper port)
				throws CapabilityNotFoundException {
			getLinkAdministration().setDestPort(port.getResource());
		}

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
