package org.mqnaas.extensions.openstack.capabilities.impl;

/*
 * #%L
 * MQNaaS :: OpenStack Implementation
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.credentials.Credentials;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.ApplicationNotFoundException;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.extensions.openstack.jclouds.clientprovider.IJCloudsNovaClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;

/**
 * <p>
 * Specific implementation of the {@link IRootResourceProvider} capability for Openstack cloud managers.
 * </p>
 * <p>
 * This capability manages the VMs of Openstack, by creating a {@link IRootResource} instance for each {@link Server} Openstack contains. These
 * resources are created during activation method, by using the client provided by {@link IJCloudsNovaClientProvider} to communicate with Openstack.
 * </p>
 * 
 * @author Adrian Rosello Rey (i2CAT)
 *
 */
public class OpenstackRootResourceProvider implements IRootResourceProvider {

	private static final Logger			log				= LoggerFactory.getLogger(OpenstackRootResourceProvider.class);

	public static final String			ZONE_ATTRIBUTE	= "zone";

	/**
	 * Set of Openstack Virtual Machines managed by this capability. Key->resourceId, Value->rootResourceInstance
	 */
	private Map<String, IRootResource>	vms;

	private NovaApi						novaClient;

	@DependingOn(core = true)
	IResourceManagementListener			resourceManagementListener;

	@DependingOn
	IJCloudsNovaClientProvider			jcloudsClientProvider;

	@DependingOn(core = true)
	IResourceManagementListener			rmListener;

	@DependingOn(core = true)
	IServiceProvider					serviceProvider;

	@Resource
	IRootResource						resource;

	public static boolean isSupporting(IRootResource resource) {
		Specification resourceSpec = resource.getDescriptor().getSpecification();

		return (resourceSpec.getType().equals(Type.CLOUD_MANAGER) && resourceSpec.getModel().equals("openstack"));
	}

	@Override
	public void activate() throws ApplicationActivationException {

		log.info("Initializing OpenstackRootResourceProvider capability for resource " + resource.getId());

		try {
			novaClient = jcloudsClientProvider.getClient(resource);
		} catch (EndpointNotFoundException e) {
			throw new ApplicationActivationException("Could not instantiate JClouds client.", e);
		}

		try {
			initializeVms();
		} catch (Exception e) {
			throw new ApplicationActivationException("Cloud not initialize Openstack VMs.", e);
		}
		log.info("Initialized OpenstackRootResourceProvider capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing OpenstackRootResourceProvider capability from resource " + resource.getId());

		for (IRootResource vm : vms.values())
			resourceManagementListener.resourceRemoved(vm, this, IRootResourceProvider.class);

		vms.clear();

		try {
			Closeables.close(novaClient, true);
		} catch (IOException e) {
			log.warn("Could not close jclouds nova client.", e);
		}
	}

	@Override
	public List<IRootResource> getRootResources() {
		log.info("Getting all IRootResources managed by resource " + resource.getId());
		return new ArrayList<IRootResource>(vms.values());
	}

	@Override
	public List<IRootResource> getRootResources(Type type, String model, String version) throws ResourceNotFoundException {
		log.info("Getting all IRootResources managed by resource " + resource.getId() + " [type= " + type + ", model=" + model + ", version=" + version + "]");

		if (type == null && StringUtils.isEmpty(model) && StringUtils.isEmpty(version)) {
			log.debug("Filter is null. Returning all IRootResources managed by resource " + resource.getId());
			return getRootResources();
		}

		List<IRootResource> resources = new ArrayList<IRootResource>();

		for (IRootResource vm : getRootResources()) {
			Specification vmSpec = vm.getDescriptor().getSpecification();

			if (vmSpec.getType().equals(type) && vmSpec.getModel().equals(model) && vmSpec.getVersion().equals(version))
				resources.add(vm);
		}

		return resources;

	}

	@Override
	public IRootResource getRootResource(String id) throws ResourceNotFoundException {

		if (StringUtils.isEmpty(id))
			throw new IllegalArgumentException("Valid id is required to retrieve specific IRootResource instance.");

		if (vms.get(id) == null)
			throw new ResourceNotFoundException("No resource found with id: " + id);

		return vms.get(id);
	}

	@Override
	public void setRootResources(Collection<IRootResource> rootResources) {
		throw new UnsupportedOperationException("The OpenstackRootResourceProvidder capability does not allow this operation.");

	}

	/**
	 * Creates {@link IRootResource}s for each {@link Server} Openstack manages.
	 */
	private void initializeVms() throws InstantiationException, IllegalAccessException, CapabilityNotFoundException, ApplicationNotFoundException {

		vms = new ConcurrentHashMap<String, IRootResource>();

		for (String zone : novaClient.getConfiguredZones()) {
			log.debug("Reading Openstack VMs [zone=" + zone + "]");

			ServerApi serverClient = novaClient.getServerApiForZone(zone);
			for (Server server : serverClient.listInDetail().concat()) {

				IRootResource rootResource = createVmRepresentation();
				resourceManagementListener.resourceAdded(rootResource, this, IRootResourceProvider.class);

				log.debug("Instantied Openstack VM [MqNaas-id=" + rootResource.getId() + ", Openstack-id=" + server.getId() + "]");

				IAttributeStore attrStore = serviceProvider.getCapability(rootResource, IAttributeStore.class);

				attrStore.setAttribute(ZONE_ATTRIBUTE, zone);
				attrStore.setAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID, server.getId());
				attrStore.setAttribute(IAttributeStore.RESOURCE_EXTERNAL_NAME, server.getName());

				vms.put(rootResource.getId(), rootResource);
			}
		}

		log.info("Initialized " + vms.size() + " VMs managed by resource " + resource.getId());

	}

	/**
	 * Instantiates a {@link IRootResource} of {@link Type#HOST} type and "openstack" model. The endpoints of this resource are inherit from the
	 * resource this capability is bound to, as well as its {@link Credentials}
	 */
	private IRootResource createVmRepresentation() throws InstantiationException, IllegalAccessException {

		Collection<Endpoint> openstackEndpoints = new ArrayList<Endpoint>(resource.getDescriptor().getEndpoints());
		Specification resourceSpec = new Specification(Type.HOST, "openstack");

		IRootResource vm = new RootResource(RootResourceDescriptor.create(resourceSpec, openstackEndpoints));
		vm.getDescriptor().setCredentials(resource.getDescriptor().getCredentials());

		return vm;
	}
}
