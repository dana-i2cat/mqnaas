package org.mqnaas.extensions.odl.capabilities.impl;

/*
 * #%L
 * MQNaaS :: ODL Capabilities
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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.client.cxf.ICXFAPIProvider;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProviderFactory;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.core.api.Endpoint;
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
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.impl.AttributeStore;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.extensions.odl.client.switchnorthbound.ISwitchNorthboundAPI;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.Node.NodeType;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnectorProperties;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnectors;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeProperties;
import org.mqnaas.extensions.odl.client.switchnorthbound.api.Nodes;
import org.mqnaas.network.api.topology.port.IPortManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * {@link IRootResourceProvider} implementation for networks managed by OpenDaylighy
 * </p>
 * <p>
 * This capability read and manages the {@link IRootResource}s available inside an MQNaaS instance of an Opendylight network. On activation, the
 * capability uses the {@link ISwitchNorthboundAPI} client to retrieve the nodes from Opendaylight and create representations of these devices with
 * {@link IRootResource}s instances.
 * </p>
 * <p>
 * Current implementation does only read devices once (when activating the capability.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ODLRootResourceProvider implements IRootResourceProvider {

	private static final Logger		log						= LoggerFactory.getLogger(ODLRootResourceProvider.class);

	private static final String		DEFAULT_CONNECTOR_NAME	= "default";

	@Resource
	IRootResource					resource;

	@DependingOn
	IServiceProvider				serviceProvider;

	@DependingOn
	IAPIClientProviderFactory		apiProviderFactory;

	@DependingOn
	IResourceManagementListener		rmListener;

	private List<IRootResource>		resources;

	private ISwitchNorthboundAPI	odlClient;

	public static boolean isSupporting(IRootResource resource) {
		Specification specification = resource.getDescriptor().getSpecification();

		return ((specification.getType() == Type.NETWORK) && (StringUtils.equals("odl", specification.getModel())));
	}

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing ODLRootResourceProvider for resource " + resource.getId());

		try {
			odlClient = apiProviderFactory.getAPIProvider(ICXFAPIProvider.class).getAPIClient(resource, ISwitchNorthboundAPI.class);
		} catch (EndpointNotFoundException e) {
			log.error("Error activating ODLRootResourceProvider capability: Endpoint could not be found", e);
			throw new ApplicationActivationException(e);
		} catch (ProviderNotFoundException e) {
			log.error("Error activating ODLRootResourceProvider capability: Client Provider could not be found", e);
			throw new ApplicationActivationException(e);
		}

		try {
			initializeResources();
		} catch (Exception e) {
			log.error("Could not initialize RootResources in odl network " + resource.getId(), e);
			throw new ApplicationActivationException("Could not initialize RootResources in odl network " + resource.getId(), e);
		}

		log.info("Initialized ODLRootResourceProvider for resource " + resource.getId());
	}

	@Override
	public void deactivate() {
		log.info("Removing ODLRootResourceProvider from resource " + resource.getId());
		resources.clear();
		log.info("Removed ODLRootResourceProvider from resource " + resource.getId());
	}

	@Override
	public List<IRootResource> getRootResources() {
		log.info("Getting all RootResources");
		return new ArrayList<IRootResource>(resources);
	}

	@Override
	public List<IRootResource> getRootResources(Type type, String model, String version) throws ResourceNotFoundException {
		log.info("Getting al RootResources with filter [type=" + type + ",model=" + model + ",version=" + version + "]");

		List<IRootResource> filteredResources = new ArrayList<IRootResource>();

		for (IRootResource resource : getRootResources()) {

			Specification specification = resource.getDescriptor().getSpecification();

			boolean matches = true;
			matches &= type != null ? specification.getType().equals(type) : true;
			matches &= model != null ? specification.getModel().equals(model) : true;
			matches &= version != null ? specification.getVersion().equals(version) : true;

			if (matches)
				filteredResources.add(resource);

		}

		log.debug("Found " + filteredResources.size() + " resources matching filter [type=" + type + ",model=" + model + ",version=" + version + "]");

		return filteredResources;
	}

	@Override
	public IRootResource getRootResource(String id) throws ResourceNotFoundException {

		if (StringUtils.isEmpty(id))
			throw new NullPointerException("Id of the resource to be found can't be null.");

		for (IRootResource resource : resources) {
			if (StringUtils.equals(id, resource.getId()))
				return resource;
		}

		throw new ResourceNotFoundException("No resource found with id: " + id);
	}

	@Override
	public void setRootResources(Collection<IRootResource> rootResources) {
		throw new UnsupportedOperationException("Service not implemented for ODL networks.");
	}

	private void initializeResources() throws InstantiationException, IllegalAccessException, CapabilityNotFoundException,
			ApplicationNotFoundException {
		log.info("Initializing ODL resources for ODL network " + resource.getId());

		resources = new CopyOnWriteArrayList<IRootResource>();

		Nodes nodes = odlClient.getNodes(DEFAULT_CONNECTOR_NAME);

		for (NodeProperties nodeProperties : nodes.getNodeProperties()) {
			// get information from the node
			String nodeId = nodeProperties.getNode().getNodeID();
			Type nodeType = parseNodeType(nodeProperties.getNode().getNodeType());

			// initialize new RootResource representing this node.
			Collection<Endpoint> odlResourceEndpoints = new ArrayList<Endpoint>(resource.getDescriptor().getEndpoints());
			IRootResource odlResource = new RootResource(RootResourceDescriptor.create(new Specification(nodeType), odlResourceEndpoints));
			rmListener.resourceAdded(odlResource, this, IRootResourceProvider.class);

			// create ports
			IPortManagement portMgm = serviceProvider.getCapability(odlResource, IPortManagement.class);
			NodeConnectors nodePorts = odlClient.getNodeConnectors(DEFAULT_CONNECTOR_NAME, nodeProperties.getNode().getNodeType().toString(), nodeId);
			for (NodeConnectorProperties nodePortProperties : nodePorts.getNodeConnectorProperties()) {
				String nodePortId = nodePortProperties.getNodeConnector().getNodeConnectorID();
				String nodePortName = nodePortProperties.getProperties().get("name").getValue();
				IResource port = portMgm.createPort();
				IAttributeStore portAttributeStore = serviceProvider.getCapability(port, IAttributeStore.class);
				portAttributeStore.setAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID, nodePortId);
				portAttributeStore.setAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME, nodePortName);
			}

			// stores the map between the created RootResource and the odl node id
			IAttributeStore attributeStore = serviceProvider.getCapability(odlResource, IAttributeStore.class);
			attributeStore.setAttribute(AttributeStore.RESOURCE_EXTERNAL_ID, nodeId);

			// add resource to resources list.
			resources.add(odlResource);

		}

		log.info("Initialized ODL resources in ODL network " + resource.getId());

	}

	private Type parseNodeType(NodeType nodeType) {
		return (nodeType.equals(NodeType.OF) ? Specification.Type.OF_SWITCH : Specification.Type.OTHER);
	}
}
