package org.mqnaas.network.impl;

/*
 * #%L
 * MQNaaS :: Network Implementation
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.ApplicationNotFoundException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.slicing.Cube;
import org.mqnaas.core.api.slicing.ISliceProvider;
import org.mqnaas.core.api.slicing.ISlicingCapability;
import org.mqnaas.core.api.slicing.SlicingException;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.core.impl.slicing.Slice;
import org.mqnaas.core.impl.slicing.Unit;
import org.mqnaas.network.api.exceptions.NetworkCreationException;
import org.mqnaas.network.api.exceptions.NetworkReleaseException;
import org.mqnaas.network.api.request.IRequestBasedNetworkManagement;
import org.mqnaas.network.api.topology.link.ILinkManagement;
import org.mqnaas.network.api.topology.port.INetworkPortManagement;
import org.mqnaas.network.api.topology.port.IPortManagement;
import org.mqnaas.network.impl.request.Request;
import org.mqnaas.network.impl.request.RequestRootResource;
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

	@DependingOn(core = true)
	private IResourceManagementListener		resourceManagementListener;

	@DependingOn(core = true)
	IServiceProvider						serviceProvider;

	@Resource
	IResource								resource;

	// stores relationship between network IRootResource <-> RequestResource
	private Map<IRootResource, IResource>	networks;

	// stores relationship between RequestRootResources <-> virtual IResource (resources created in this capability for this RequestRootResource)
	private Map<IResource, IResource>		resourceMapping;

	@Override
	public IRootResource createNetwork(IResource requestResource) throws NetworkCreationException {

		checkRequest(requestResource);

		IRootResource networkResource = null;

		// this structure saves all the resources of the new virtual network
		List<IRootResource> virtualNetworkResources = new ArrayList<IRootResource>();

		// this structure saves all virtual subnetworks created by the underlaying networks, so we can remove them on the release method.
		List<IResource> virtualSubnetworks = new ArrayList<IResource>();

		try {
			networkResource = new RootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK, "virtual")));
			Network virtualNetwork = new Network(networkResource, serviceProvider);

			// Manual bind, so that capabilities exist
			resourceManagementListener.resourceAdded(virtualNetwork.getNetworkResource(), this, IRequestBasedNetworkManagement.class);

			Request request = new Request(requestResource, serviceProvider);

			// this map will store relationship between virtualPorts and requestPorts, needed when creating the network links
			Map<IResource, IResource> virtualPortMapping = new HashMap<IResource, IResource>();

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

					IResource createdResource = createSlice(virtualNetwork, phyResource, virtualResource);

					// store mapping to request resource
					resourceMapping.put(virtualResource.getResource(), createdResource);

					// create ports in this resource
					createResourcePorts(virtualResource.getPorts(), createdResource, request, virtualPortMapping);

					// add created resource to list of resources to be reserved
					virtualNetworkResources.add((IRootResource) createdResource);

				}

				// If the resource is a subnetwork, create request and delegate to it
				IRequestBasedNetworkManagement subnetManagementCapability = phyResource.getRequestBasedNetworkManagementCapability();
				if (subnetManagementCapability != null) {
					Network subNetwork = new Network(virtualResource.getResource(), serviceProvider);
					Network virtualSubNetwork = delegateToSubnetwork(request, subNetwork);
					virtualNetworkResources.add((IRootResource) virtualSubNetwork.getNetworkResource());
					virtualSubnetworks.add(virtualNetwork.getNetworkResource());

					resourceMapping.put(virtualSubNetwork.getNetworkResource(), virtualResource.getResource());
				}

			}

			createNetworkLinks(networkResource, request, virtualPortMapping);
			defineNetworkPorts(networkResource, request);

			// TODO in the future, reserve

			// add resources to virtual network
			virtualNetwork.setRootResources(virtualNetworkResources);

			networks.put(networkResource, requestResource);
			// netSubnetworks.putAll(networkResource, virtualSubnetworks);

		} catch (InstantiationException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		} catch (IllegalAccessException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		} catch (CapabilityNotFoundException c) {
			throw new NetworkCreationException("Network creation failed.", c);
		} catch (SlicingException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		} catch (ApplicationNotFoundException e) {
			throw new NetworkCreationException("Network creation failed.", e);
		}

		return networkResource;
	}

	private void checkRequest(IResource requestResource) throws NetworkCreationException {

		Request request = new Request(requestResource, serviceProvider);

		// check period is specified
		if (request.getPeriod() == null)
			throw new NetworkCreationException("Period must be defined");

		// check resources has units/ranges/cubes
		for (IResource rootResource : request.getRootResources()) {
			// subnetworks have no slice, the rest must have a slice
			if (rootResource instanceof IRootResource &&
					!((IRootResource) rootResource).getDescriptor().getSpecification().getType().equals(Type.NETWORK)) {

				NetworkSubResource subResource = new NetworkSubResource(rootResource, serviceProvider);
				if (subResource.getSlice() == null)
					throw new NetworkCreationException("Slice must be defined");

				Slice slice = new Slice(subResource.getSlice(), serviceProvider);
				if (slice.getUnits() == null || slice.getUnits().isEmpty())
					throw new NetworkCreationException("Slice units must be defined");
				for (Unit unit : slice.getUnits()) {
					if (unit.getName() == null || unit.getName().isEmpty())
						throw new NetworkCreationException("Each slice unit must have a name");
					if (unit.getRange() == null)
						throw new NetworkCreationException("Each slice unit must have a range");
				}

				if (slice.getCubes() == null || slice.getCubes().isEmpty())
					throw new NetworkCreationException("Slice cubes must be defined");
			}
		}
	}

	private void createResourcePorts(List<IResource> virtualResourcePorts, IResource resource, Request request,
			Map<IResource, IResource> virtualPortMapping) throws CapabilityNotFoundException,
			ApplicationNotFoundException {

		NetworkSubResource resourceWrapper = new NetworkSubResource(resource, serviceProvider);

		for (IResource port : virtualResourcePorts) {

			PortResourceWrapper physicalPort = new PortResourceWrapper(request.getMappedDevice(port), serviceProvider);
			PortResourceWrapper newPort = new PortResourceWrapper(resourceWrapper.createPort(), serviceProvider);

			resourceManagementListener.resourceAdded(newPort.getPortResource(),
					serviceProvider.getCapability(resource, IPortManagement.class), IPortManagement.class);

			newPort.setAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID, physicalPort.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));

			virtualPortMapping.put(port, newPort.getPortResource());
		}
	}

	private Network delegateToSubnetwork(Request request, Network subnetwork) throws CapabilityNotFoundException,
			NetworkCreationException {

		// generate request
		IResource phynetworkResource = request.getMappedDevice(subnetwork.getNetworkResource());
		Network phyNetwork = new Network(phynetworkResource, serviceProvider);

		IResource subNetRequestresource = phyNetwork.createRequest();
		Request subnetRequest = new Request(subNetRequestresource, serviceProvider);

		// get subnetwork resources and fill the request with the resources
		for (IResource subnetResource : subnetwork.getNetworkSubResources()) {
			IResource virtResource = subnetRequest.createResource(((RequestRootResource) subnetResource).getType());
			IResource phySubResource = request.getMappedDevice(subnetResource);
			subnetRequest.defineMapping(virtResource, phySubResource);
		}

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

		// set period
		subnetRequest.setPeriod(request.getPeriod());

		IRootResource virtualNetResource = phyNetwork.createVirtualNetwork(subnetRequest.getRequestResource());

		return new Network(virtualNetResource, serviceProvider);
	}

	private void createNetworkLinks(IRootResource networkResource, Request request, Map<IResource, IResource> portMapping)
			throws CapabilityNotFoundException {
		// links
		ILinkManagement networkLinkManagement = serviceProvider.getCapability(networkResource, ILinkManagement.class);

		List<IResource> requestLinks = request.getLinks();
		for (IResource link : requestLinks) {

			Link netLink = new Link(networkLinkManagement.createLink(), serviceProvider);
			Link reqLink = new Link(link, serviceProvider);

			IResource srcPort = portMapping.get(reqLink.getSrcPort());
			IResource dstPort = portMapping.get(reqLink.getDstPort());

			netLink.setSrcPort(srcPort);
			netLink.setDstPort(dstPort);
		}
	}

	private void defineNetworkPorts(IRootResource networkResource, Request request)
			throws CapabilityNotFoundException {

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
			throws SlicingException, CapabilityNotFoundException, ApplicationNotFoundException {

		ISlicingCapability phySlicingCapab = phyResource.getSlicingCapability();

		ISliceProvider phySliceProviderCapab = phyResource.getSliceProviderCapability();
		ISliceProvider virtSliceProviderCapab = virtualResource.getSliceProviderCapability();

		Slice phySlice = new Slice(phySliceProviderCapab.getSlice(), serviceProvider);
		Slice virtSlice = new Slice(virtSliceProviderCapab.getSlice(), serviceProvider);

		// create slice
		IResource newResource = phySlicingCapab.createSlice(virtSlice.getResource());

		// manual bind of the created slice to virtualnetwork
		resourceManagementListener.resourceAdded(newResource,
				serviceProvider.getCapabilityInstance(virtualNetwork.getNetworkResource(), IRootResourceProvider.class), IRootResourceProvider.class);

		// add unknown value to RESOURCE_EXTERNAL_ID attribute for sliced resource
		getAttributeStore(newResource).setAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID, IAttributeStore.UNKNOWN_VALUE);

		// remove slice information from physical
		phySlice.cut(virtSlice);

		NetworkSubResource newSubnetResource = new NetworkSubResource(newResource, serviceProvider);
		ISliceProvider sliceProvider = newSubnetResource.getSliceProviderCapability();
		Slice newResourceSlice = new Slice(sliceProvider.getSlice(), serviceProvider);
		for (Unit unit : virtSlice.getUnits()) {

			Unit unit2 = newResourceSlice.addUnit(unit.getName());
			unit2.setRange(unit.getRange());

		}
		newResourceSlice.setCubes(new ArrayList<Cube>(virtSlice.getCubes()));

		return newResource;
	}

	@Override
	public void releaseNetwork(IRootResource network) throws NetworkReleaseException {

		if (network == null || !networks.keySet().contains(network))
			throw new IllegalArgumentException("Can only release networks managed by this capability instance.");

		Request originalRequest = new Request(networks.get(network), serviceProvider);
		try {
			for (IResource requestResource : originalRequest.getRootResources()) {
				// get mapped resources (phy + virtual)
				IResource virtualResource = resourceMapping.get(requestResource);
				IResource physicalResource = originalRequest.getMappedDevice(requestResource);

				// create wrappers for all resources
				NetworkSubResource reqResource = new NetworkSubResource(requestResource, serviceProvider);
				NetworkSubResource virtResource = new NetworkSubResource(virtualResource, serviceProvider);
				NetworkSubResource phyResource = new NetworkSubResource(physicalResource, serviceProvider);

				// I) slices
				if (phyResource.getSlicingCapability() != null) {

					// create slice wrappers
					Slice virtSlice = new Slice(virtResource.getSlice(), serviceProvider);
					Slice phySlice = new Slice(phyResource.getSlice(), serviceProvider);

					// return cube to original slice
					phySlice.add(virtSlice);

					// remove slice and sliced resource
					phyResource.getSlicingCapability().removeSlice(virtResource.getResource());
					resourceManagementListener.resourceRemoved(virtResource.getResource(), phyResource.getSlicingCapability(),
							ISlicingCapability.class);

				}

				// II) subnetworks
				if (reqResource.getRequestBasedNetworkManagementCapability() != null) {

					IResource virtNetworkResource = resourceMapping.get(reqResource.getResource());
					IResource phyNetworkResource = originalRequest.getMappedDevice(reqResource.getResource());
					Network phyNetwork = new Network(phyNetworkResource, serviceProvider);
					phyNetwork.releaseVirtualNetwork((IRootResource) virtNetworkResource);
				}

				resourceMapping.remove(requestResource);

			}
		} catch (SlicingException s) {
			throw new NetworkReleaseException("Network creation failed.", s);
		}

		// remove network
		resourceManagementListener.resourceRemoved(network, this, IRequestBasedNetworkManagement.class);
		networks.remove(network);

	}

	@Override
	public Collection<IRootResource> getNetworks() {
		return new ArrayList<IRootResource>(networks.keySet());
	}

	@Override
	public void activate() throws ApplicationActivationException {
		networks = new ConcurrentHashMap<IRootResource, IResource>();
		resourceMapping = new ConcurrentHashMap<IResource, IResource>();
	}

	@Override
	public void deactivate() {
	}

	private IAttributeStore getAttributeStore(IResource resource) throws CapabilityNotFoundException {
		return serviceProvider.getCapability(resource, IAttributeStore.class);
	}

}
