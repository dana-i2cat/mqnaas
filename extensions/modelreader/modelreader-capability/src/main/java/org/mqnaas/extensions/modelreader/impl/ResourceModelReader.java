package org.mqnaas.extensions.modelreader.impl;

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.extensions.modelreader.api.IResourceModelReader;
import org.mqnaas.extensions.modelreader.api.ResourceModelWrapper;
import org.mqnaas.extensions.odl.capabilities.flows.IFlowManagement;
import org.mqnaas.network.impl.topology.link.LinkResource;
import org.mqnaas.network.impl.topology.port.PortResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adrián Roselló Rey
 * 
 */
public class ResourceModelReader implements IResourceModelReader {

	private static final Logger	log	= LoggerFactory.getLogger(ResourceModelReader.class);

	@DependingOn
	IServiceProvider			serviceProvider;

	@DependingOn
	IExecutionService			serviceExecution;

	@Resource
	IResource					resource;

	public static boolean isSupporting(IResource resource) {
		return true;
	}

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing ResourceModelReader capability for resource " + resource.getId());
		log.info("Initialized ResourceModelReader capability for resource " + resource.getId());
	}

	@Override
	public void deactivate() {
		log.info("Removing ResourceModelReader capability from resource " + resource.getId());
		log.info("Removing ResourceModelReader capability from resource " + resource.getId());
	}

	@Override
	public ResourceModelWrapper getResourceModel() {

		// create wrapper with the resource id and set its type
		ResourceModelWrapper modelWrapper = new ResourceModelWrapper(resource.getId());
		setResourceType(modelWrapper, resource);

		// we look for all management capabilities bound to this resource. They contain a method with annotation "listResources"
		for (Class<? extends ICapability> capabilityClass : serviceProvider.getCapabilities(resource)) {
			try {

				if (capabilityClass.equals(IAttributeStore.class)) {
					IAttributeStore attributeStoreCapab = serviceProvider.getCapability(resource, IAttributeStore.class);
					modelWrapper.setAttributes(attributeStoreCapab.getAttributes().getMap());

				}
				// FIXME this should be generalized.
				else if (capabilityClass.equals(IFlowManagement.class)) {
					IFlowManagement flowMgmCapab = serviceProvider.getCapability(resource, IFlowManagement.class);
					modelWrapper.setConfiguredRules(flowMgmCapab.getAllFlows());
				}
				else {

					Method listResourcesMethod = getListResourcesMethod(capabilityClass);

					if (listResourcesMethod != null) {

						// invoke the method annotated with "listResoures"
						IService listResourcesService = serviceProvider.getService(resource, listResourcesMethod.getName(),
								listResourcesMethod.getParameterTypes());

						@SuppressWarnings("unchecked")
						// safe casting
						List<IResource> managedResources = (List<IResource>) serviceExecution.execute(listResourcesService, null);
						List<ResourceModelWrapper> subResources = new ArrayList<ResourceModelWrapper>();

						// for each resource returned by the management capability, call its ResourceModelReader capability
						for (IResource managedResource : managedResources) {

							// This filter has been introduced to avoid an infinite recursion over MQNaaS-Core resource, since its
							// IRootResourceProvider capability returns the MQNaaS-Core as well in the list of its managed resources.
							if (!managedResource.equals(resource)) {
								IResourceModelReader subResourceModelReader = serviceProvider.getCapability(managedResource,
										IResourceModelReader.class);
								subResources.add(subResourceModelReader.getResourceModel());
							}
						}
						modelWrapper.getResources().addAll(subResources);
					}
				}

			} catch (Exception e) {
				throw new IllegalStateException("Could not get current state of resource " + resource.getId(), e);
			}
		}
		return modelWrapper;

	}

	/**
	 * This method retrieves the method annotated with {@link ListsResources} inside a management {@link ICapability}
	 *
	 * TODO move this method to a generic helper in MQNaaS core
	 * 
	 * @param capabilityClass
	 *            Class representing a management capability.
	 * @return {@link Method} containing the <code>ListResources</code> annotation. <code>Null</code> If there's no method with this annotation.
	 */
	private Method getListResourcesMethod(Class<? extends ICapability> capabilityClass) {
		for (Method method : capabilityClass.getMethods()) {
			for (Annotation annotation : Arrays.asList(method.getAnnotations())) {
				if (annotation instanceof ListsResources)
					return method;
			}
		}

		return null;
	}

	/**
	 * Sets the type of a {@link IResource} inside the {@link ResourceModelWrapperl} instance.
	 * 
	 * @param modelWrapper
	 *            Instance of <code>ResourceModelWrapper</code> containing the main information about the given <code>resource</code>, and where the
	 *            resource type should be set.
	 * @param resource
	 *            Instance of the {@link IResource} which type should be parsed.
	 */
	private void setResourceType(ResourceModelWrapper modelWrapper, IResource resource) {

		if (resource instanceof IRootResource) {
			IRootResource rootResource = (IRootResource) resource;
			modelWrapper.setType(rootResource.getDescriptor().getSpecification().getType().toString());
		}
		// FIXME. everytime we add a new IResource implementation, this method should be extended. We have to think a better way to parse the type of
		// non-IRootResources resources.
		else if (resource instanceof PortResource) {
			modelWrapper.setType("port");
		} else if (resource instanceof LinkResource) {
			modelWrapper.setType("link");
		}

	}
}
