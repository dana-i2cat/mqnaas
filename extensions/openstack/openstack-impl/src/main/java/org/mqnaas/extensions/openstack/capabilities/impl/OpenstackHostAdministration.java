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

import org.apache.commons.lang3.StringUtils;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.mqnaas.clientprovider.api.client.IClientProviderFactory;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.extensions.openstack.capabilities.host.api.IHostAdministration;
import org.mqnaas.extensions.openstack.jclouds.clientprovider.IJCloudsNovaClientProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;

/**
 * <p>
 * Specific implementation of the {@link IHostAdministration} capability for Openstack virtual machines.
 * </p>
 * 
 * <p>
 * This capability does not store any information about the {@link Server}. Every getter method uses the Jclouds client in order to retrieve this
 * information from OpenStack.
 * </p>
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class OpenstackHostAdministration implements IHostAdministration {

	private static final Logger	log	= LoggerFactory.getLogger(OpenstackHostAdministration.class);

	private NovaApi				novaClient;

	@DependingOn
	IAttributeStore				attributeStore;

	@DependingOn
	IClientProviderFactory		clientProviderFactory;

	@Resource
	IRootResource				resource;

	public static boolean isSupporting(IRootResource resource) {
		Specification resourceSpec = resource.getDescriptor().getSpecification();

		return (resourceSpec.getType().equals(Type.HOST) && resourceSpec.getModel().equals("openstack"));
	}

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing OpenstackHostAdministration capability for resource " + resource.getId());

		try {
			novaClient = clientProviderFactory.getClientProvider(IJCloudsNovaClientProvider.class).getClient(resource);
		} catch (EndpointNotFoundException e) {
			throw new ApplicationActivationException("Could not instantiate JClouds client.", e);
		} catch (ProviderNotFoundException e) {
			throw new ApplicationActivationException("Could not instantiate JClouds client.", e);
		}

		log.info("Initialized OpenstackHostAdministration capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing OpenstackHostAdministration capability from resource " + resource.getId());

		try {
			Closeables.close(novaClient, true);
		} catch (IOException e) {
			log.warn("Could not close jclouds nova client.", e);
		}

		log.info("Removed OpenstackHostAdministration capability from resource " + resource.getId());

	}

	@Override
	public int getNumberOfCpus() {
		log.debug("Getting number of cpus of host [id=" + resource.getId() + "]");

		Server server = getServer();
		Flavor flavor = (Flavor) server.getFlavor();

		return flavor.getVcpus();
	}

	@Override
	public int getMemorySize() {
		log.debug("Getting memory size of host [id=" + resource.getId() + "]");

		Server server = getServer();
		Flavor flavor = (Flavor) server.getFlavor();

		return flavor.getRam();
	}

	@Override
	public int getDiskSize() {
		log.debug("Getting disk size of host [id=" + resource.getId() + "]");

		Server server = getServer();
		Flavor flavor = (Flavor) server.getFlavor();

		return flavor.getDisk();
	}

	@Override
	public String getSwapSize() {

		Server server = getServer();
		Flavor flavor = (Flavor) server.getFlavor();

		return flavor.getSwap() != null ? flavor.getSwap().get() : null;
	}

	/**
	 * Retrieves the {@link Server} instance represented by the injected <code>resource</code> by using the jclouds client.
	 * 
	 * @throws IllegalStateException
	 *             <ul>
	 *             <li>If {@link IAttributeStore} capability of the injected resource does not contain information about the {@link Server} id and the
	 *             zone it belongs to.</li>
	 *             <li>If there's no zone identified by the zoneId stored in IAttributeStore capability.</li>
	 *             <li>If there's no server identified by the id stored in IAttributeStore capability.</li>
	 *             </ul>
	 */
	private Server getServer() {

		String zone = attributeStore.getAttribute(OpenstackRootResourceProvider.ZONE_ATTRIBUTE);
		String vmId = attributeStore.getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID);

		if (StringUtils.isEmpty(zone) || StringUtils.isEmpty(vmId))
			throw new IllegalStateException("Can't read VM cpus if AttributeStore does not contain its external id and the zone it belongs to.");

		ServerApi serverClient = novaClient.getServerApiForZone(zone);

		if (serverClient == null)
			throw new IllegalStateException("There's no configured zone with such id [zone=" + zone + "]");

		Server server = serverClient.get(vmId);
		if (server == null)
			throw new IllegalStateException("There's no server with such id in this zone [serverId=" + vmId + ", zone=" + zone + "]");

		return server;
	}

}
