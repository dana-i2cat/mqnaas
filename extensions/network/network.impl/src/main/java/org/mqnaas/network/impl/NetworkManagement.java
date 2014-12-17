package org.mqnaas.network.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.i2cat.dana.mqnaas.capability.reservation.IReservationCapability;
import net.i2cat.dana.mqnaas.capability.reservation.exception.ResourceReservationException;
import net.i2cat.dana.mqnaas.capability.reservation.model.Device;

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
import org.mqnaas.core.api.slicing.Unit;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.network.api.exceptions.NetworkCreationException;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.api.request.Period;
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

					IResource newResource = createSlice(phyResource, virtualResource);

					// add created resource to list of resources to be reserved
					resourcesToReserve.add((IRootResource) newResource);

				}

				// If the resource is a subnetwork, create request and delegate to it
				IRequestBasedNetworkManagement subnetManagementCapability = virtualResource.getRequestBasedNetworkManagementCapability();
				if (subnetManagementCapability != null) {
					IResource subnetRequest = new RequestResource();
					// TODO what to fill?
					IRootResource subnetPhyResource = subnetManagementCapability.createNetwork(subnetRequest);
					Network subnetwork = new Network(subnetPhyResource, serviceProvider);
					List<IRootResource> subnetResources = subnetwork.getResources();
					resourcesToReserve.addAll(subnetResources);

				}

			}

			createNetworkLinks(networkResource, request);
			defineNetworkPorts(networkResource, request);
			// reserve!!
			Period period = request.getPeriod();
			Set<Device> devices = new HashSet<Device>();

			for (IRootResource iRootResource : resourcesToReserve) {
				Device device = new Device();
				device.setId(iRootResource.getId());
				device.setType(iRootResource.getDescriptor().getSpecification().getType());
				devices.add(device);
			}

			reservationCapability.createReservation(devices, period);

			// add resources to network
			// TODO be aware of nitos resources
			serviceProvider.getCapability(networkResource, IRootResourceProvider.class).setRootResources(resourcesToReserve);

			networks.add(networkResource);

		} catch (InstantiationException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		} catch (IllegalAccessException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		} catch (CapabilityNotFoundException c) {
			throw new NetworkCreationException("Network creation failed.", c);
		} catch (ResourceReservationException r) {
			throw new NetworkCreationException("Network creation failed.", r);
		} catch (SlicingException e) {
			throw new NetworkCreationException("Network creation failed.", e);

		}

		return networkResource;
	}

	private void createNetworkLinks(IRootResource networkResource, Request request) throws CapabilityNotFoundException {
		// links
		ILinkManagement networkLinkManagement = serviceProvider.getCapability(networkResource, ILinkManagement.class);

		List<IResource> requestLinks = request.getLinks();
		for (IResource link : requestLinks) {

			LinkWrapper netLink = new LinkWrapper(networkLinkManagement.createLink(), serviceProvider);
			LinkWrapper reqLink = new LinkWrapper(link, serviceProvider);

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

	private IResource createSlice(NetworkSubResource phyResource, NetworkSubResource virtualResource) throws SlicingException,
			CapabilityNotFoundException {

		ISlicingCapability phySlicingCapab = phyResource.getSlicingCapability();

		ISliceProvider phySliceProviderCapab = phyResource.getSliceProviderCapability();
		ISliceProvider virtSliceProviderCapab = virtualResource.getSliceProviderCapability();

		SliceWrapper phySlice = new SliceWrapper(phySliceProviderCapab.getSlice(), serviceProvider);
		SliceWrapper virtSlice = new SliceWrapper(virtSliceProviderCapab.getSlice(), serviceProvider);

		ISliceAdministration phySliceAdminCapab = phySlice.getSliceAdministration();
		ISliceAdministration virtSliceAdminCapab = virtSlice.getSliceAdministration();

		// create slice
		IResource newResource = phySlicingCapab.createSlice(virtSlice.getSlice());

		ISliceAdministration virtSliceAdminCapab2 = virtSlice.getSliceAdministration();

		// remove slice information from physical
		phySliceAdminCapab.cut(virtSlice.getSlice());

		NetworkSubResource newSubnetResource = new NetworkSubResource(newResource, serviceProvider);
		ISliceProvider sliceProvider = newSubnetResource.getSliceProviderCapability();
		SliceWrapper newResourceSlice = new SliceWrapper(sliceProvider.getSlice(), serviceProvider);
		ISliceAdministration newResourceSliceAdminCapab = newResourceSlice.getSliceAdministration();
		for (Unit unit : virtSliceAdminCapab.getUnits()) {
			Unit unit2 = new Unit(unit.getName());
			newResourceSliceAdminCapab.addUnit(unit2);
			newResourceSliceAdminCapab.setRange(unit2, virtSliceAdminCapab.getRange(unit));
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
