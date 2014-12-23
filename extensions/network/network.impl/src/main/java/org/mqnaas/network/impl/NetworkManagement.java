package org.mqnaas.network.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.i2cat.dana.mqnaas.capability.reservation.IReservationCapability;

import org.apache.commons.lang3.StringUtils;
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
import org.mqnaas.core.api.slicing.ISliceAdministration;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.core.api.slicing.SlicingException;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.core.impl.slicing.UnitResource;
import org.mqnaas.network.api.exceptions.NetworkCreationException;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;
import org.mqnaas.network.impl.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages network resources and provides services to create and release new networks based on network request.
 * 
 * @author Georg Mansky-Kummert
 */
public class NetworkManagement implements IRequestBasedNetworkManagement {

	private static final Logger	log	= LoggerFactory.getLogger(NetworkManagement.class);

	public static boolean isSupporting(IRootResource rootResource) {
		Type type = rootResource.getDescriptor().getSpecification().getType();

		return type == Type.NETWORK && !StringUtils.equals(rootResource.getDescriptor().getSpecification().getModel(), "nitos");
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
		List<IRootResource> virtualNetworkResources = new ArrayList<IRootResource>();

		try {
			networkResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK, "virtual")));
			Network virtualNetwork = new Network(networkResource, serviceProvider);

			// Manual bind, so that capabilities exist
			resourceManagementListener.resourceAdded(virtualNetwork.getNetworkResource(), this, IRequestBasedNetworkManagement.class);

			Request request = new Request(requestResource, serviceProvider);

			List<IResource> networkRootResources = request.getRootResources();
			for (IResource netRootResource : networkRootResources) {

				// get the physical resource (device is mapped to this resource)
				IRootResource phyRootResource = (IRootResource) request.getMappedDevice(netRootResource);

				// wrapp resources
				NetworkSubResource phyResource = new NetworkSubResource(phyRootResource, serviceProvider);
				NetworkSubResource virtualResource = new NetworkSubResource(netRootResource, serviceProvider);

				// create slice if it's a sliceable resource
				ISlicingCapability phySlicingCapab = phyResource.getSlicingCapability();
				if (phySlicingCapab != null) {

					IResource newResource = createSlice(virtualNetwork, phyResource, virtualResource);

					// add created resource to list of resources to be reserved
					virtualNetworkResources.add((IRootResource) newResource);

				}

				// If the resource is a subnetwork, create request and delegate to it
				IRequestBasedNetworkManagement subnetManagementCapability = virtualResource.getRequestBasedNetworkManagementCapability();
				if (subnetManagementCapability != null) {
					Network subNetwork = new Network(virtualResource.getResource(), serviceProvider);
					// List<IRootResource> resources = delegateToSubnetwork(request, subNetwork);

					// resourcesToReserve.addAll(resources);
				}

			}

			createNetworkLinks(networkResource, request);
			defineNetworkPorts(networkResource, request);

			// TODO in the future, reserve

			// add resources to virtual network
			virtualNetwork.setRootResources(virtualNetworkResources);

			networks.add(networkResource);

		} catch (InstantiationException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		} catch (IllegalAccessException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		} catch (CapabilityNotFoundException c) {
			throw new NetworkCreationException("Network creation failed.", c);
		} catch (SlicingException e) {
			throw new NetworkCreationException("Network creation failed.", e);

		}

		return networkResource;
	}

	// private List<IRootResource> delegateToSubnetwork(Request request, Network subnetwork) throws CapabilityNotFoundException,
	// NetworkCreationException {
	//
	// // generate request
	// IResource subNetRequestresource = subnetwork.createRequest();
	// Request subnetRequest = new Request(subNetRequestresource, serviceProvider);
	//
	// // get subnetwork resources and fill the request with the resources
	// for (IRootResource subnetResource : subnetwork.getResources()) {
	// IResource virtResource = request.createResource(subnetResource.getDescriptor().getSpecification().getType());
	// request.defineMapping(virtResource, subnetResource);
	// }
	//
	// // get links and fill the request with the links
	// for (IResource linkResource : subnetwork.getLinks()) {
	//
	// Link phyLink = new Link(linkResource, serviceProvider);
	// Link virtLink = new Link(request.createLink(), serviceProvider);
	//
	// IResource srcPort = request.getMappedDevice(phyLink.getSrcPort());
	// IResource dstPort = request.getMappedDevice(phyLink.getDstPort());
	//
	// virtLink.setSrcPort(srcPort);
	// virtLink.setDstPort(dstPort);
	// }
	//
	// // external links ?!
	//
	// // set period
	// subnetRequest.setPeriod(request.getPeriod());
	//
	// IRootResource virtualNetResource = subnetwork.createVirtualNetwork(subnetRequest.getRequestResource());
	//
	// Network virtualNetwork = new Network(virtualNetResource, serviceProvider);
	// List<IRootResource> subnetResources = virtualNetwork.getResources();
	//
	// // TODO what to do with the network?!?!
	// return subnetResources;
	//
	// }

	private void createNetworkLinks(IRootResource networkResource, Request request) throws CapabilityNotFoundException {
		// links
		ILinkManagement networkLinkManagement = serviceProvider.getCapability(networkResource, ILinkManagement.class);

		List<IResource> requestLinks = request.getLinks();
		for (IResource link : requestLinks) {

			Link netLink = new Link(networkLinkManagement.createLink(), serviceProvider);
			Link reqLink = new Link(link, serviceProvider);

			IResource srcPort = request.getMappedDevice(reqLink.getSrcPort());
			IResource dstPort = request.getMappedDevice(reqLink.getDstPort());

			netLink.setSrcPort(srcPort);
			netLink.setDstPort(dstPort);
		}
	}

	private void defineNetworkPorts(IRootResource networkResource, Request request) throws CapabilityNotFoundException {

		log.info("Defining network external ports for network " + networkResource.getId());

		List<IResource> reqPorts = request.getNetworkPorts();
		for (IResource reqPort : reqPorts) {

			IResource phyPort = request.getMappedDevice(reqPort);
			if (phyPort == null) {
				log.warn("Request port " + reqPort.getId() + " does not have any physical port. Skipping it...");
			}
			else {
				INetworkPortManagement netPortMgm = serviceProvider.getCapability(networkResource, INetworkPortManagement.class);
				netPortMgm.addPort(phyPort);
				log.debug("Added port " + phyPort.getId() + " to network " + networkResource.getId());
			}
		}

	}

	private IResource createSlice(Network virtualNetwork, NetworkSubResource phyResource, NetworkSubResource virtualResource)
			throws SlicingException,
			CapabilityNotFoundException {

		ISlicingCapability phySlicingCapab = phyResource.getSlicingCapability();

		ISliceProvider phySliceProviderCapab = phyResource.getSliceProviderCapability();
		ISliceProvider virtSliceProviderCapab = virtualResource.getSliceProviderCapability();

		Slice phySlice = new Slice(phySliceProviderCapab.getSlice(), serviceProvider);
		Slice virtSlice = new Slice(virtSliceProviderCapab.getSlice(), serviceProvider);

		ISliceAdministration phySliceAdminCapab = phySlice.getSliceAdministration();
		ISliceAdministration virtSliceAdminCapab = virtSlice.getSliceAdministration();

		// create slice
		IResource newResource = phySlicingCapab.createSlice(virtSlice.getSlice());

		// manual bind of the created slice to virtualnetwork
		resourceManagementListener.resourceAdded(newResource,
				serviceProvider.getCapability(virtualNetwork.getNetworkResource(), IRootResourceProvider.class), IRootResourceProvider.class);

		// remove slice information from physical
		phySliceAdminCapab.cut(virtSlice.getSlice());

		NetworkSubResource newSubnetResource = new NetworkSubResource(newResource, serviceProvider);
		ISliceProvider sliceProvider = newSubnetResource.getSliceProviderCapability();
		Slice newResourceSlice = new Slice(sliceProvider.getSlice(), serviceProvider);
		ISliceAdministration newResourceSliceAdminCapab = newResourceSlice.getSliceAdministration();
		for (IResource unit : virtSlice.getUnits()) {

			Unit virtUnitResource = new Unit(unit, serviceProvider);

			IResource unit2 = newResourceSlice.addUnit(((UnitResource) virtUnitResource.getUnit()).getName());

			Unit unitResource = new Unit(unit2, serviceProvider);
			unitResource.setRange(virtUnitResource.getRange());

		}
		newResourceSliceAdminCapab.setCubes(virtSliceAdminCapab.getCubes());

		return newResource;
	}

	@Override
	public void releaseNetwork(IRootResource network) {
		// I) Release all resource of the network
		resourceManagementListener.resourceRemoved(network, this, IRequestBasedNetworkManagement.class);

		// II) Delete the network
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
