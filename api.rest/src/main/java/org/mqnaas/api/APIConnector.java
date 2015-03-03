package org.mqnaas.api;

/*
 * #%L
 * MQNaaS :: REST API Provider
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.mqnaas.api.exceptions.InvalidCapabilityDefinionException;
import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.ICoreModelCapability;
import org.mqnaas.core.api.ICoreProvider;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IObservationService;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.impl.ApplicationInstance;
import org.mqnaas.core.impl.IBindingManagement;
import org.mqnaas.core.impl.notificationfilter.ServiceFilterWithParams;
import org.mqnaas.core.impl.resourcetree.ApplicationNode;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceNode;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connector between {@link IBindingManagement} and {@link IRESTAPIProvider}
 * 
 * This class main responsability is the automatic publication of the REST API of each ICapability and IApplication registered in the system.
 * 
 * In order to achieve this goal, this IApplication observes execution of various {@link IBindingManagement} provided services and triggers the
 * execution of appropriated services in {@link IRESTAPIProvider}
 * 
 * This class has the additional responsability of computing a path for each REST endpoint.
 * 
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public class APIConnector implements IAPIConnector, ServiceListener {

	private static final Logger					log	= LoggerFactory.getLogger(APIConnector.class);

	@DependingOn
	ICoreModelCapability						coreModelCapability;

	@DependingOn
	IObservationService							observationService;

	@DependingOn
	IServiceProvider							serviceProvider;

	@DependingOn
	ICoreProvider								coreProvider;

	@DependingOn
	IRESTAPIProvider							restApiProvider;

	/**
	 * Depending on IBindingManagement forces this application to be activated only once IBindingManagement is available, which is required to get
	 * observed services in activate() method.
	 */
	@DependingOn
	@SuppressWarnings("unused")
	IBindingManagement							bindingManagement;

	@DependingOn
	IExecutionService							executionService;

	@DependingOn
	IRootResourceProvider						rootResourceProvider;

	@DependingOn
	IRootResourceAdministration					rootResourceAdmin;

	private ServiceRegistration<IAPIConnector>	osgiRegistration;

	@Override
	public void activate() throws ApplicationActivationException {

		try {
			/**
			 * {@link IBindingManagement#bind(CapabilityNode, ResourceNode) bind}
			 */
			IService bindService = serviceProvider.getService(coreProvider.getCore(), "bind", CapabilityNode.class, ResourceNode.class);
			/**
			 * {@link IBindingManagement#unbind(CapabilityNode, ResourceNode) unbind}
			 */
			IService unbindService = serviceProvider.getService(coreProvider.getCore(), "unbind", CapabilityNode.class, ResourceNode.class);

			/**
			 * {@link IBindingManagement#addApplicationInstance(ApplicationInstance) addApplicationInstance}
			 */
			IService addApplicationInstance = serviceProvider.getService(coreProvider.getCore(), "addApplicationInstance",
					ApplicationInstance.class);
			/**
			 * {@link IBindingManagement#removeApplicationInstance(ApplicationInstance) removeApplicationInstance}
			 */
			IService removeApplicationInstance = serviceProvider.getService(coreProvider.getCore(), "removeApplicationInstance",
					ApplicationInstance.class);

			IService publishCapabilityService = serviceProvider.getApplicationService(this, "publish", CapabilityNode.class);
			IService unpublishCapabilityService = serviceProvider.getApplicationService(this, "unpublish", CapabilityNode.class);
			IService publishApplicationService = serviceProvider.getApplicationService(this, "publish", ApplicationInstance.class);
			IService unpublishApplicationService = serviceProvider.getApplicationService(this, "unpublish", ApplicationInstance.class);

			// register publishServices for them being executed after each bind
			observationService.registerObservation(new ServiceFilterWithParams(bindService), publishCapabilityService);
			observationService.registerObservation(new ServiceFilterWithParams(addApplicationInstance), publishApplicationService);

			// register unpublishServices for them being executed after each unbind
			observationService.registerObservation(new ServiceFilterWithParams(unbindService), unpublishCapabilityService);
			observationService.registerObservation(new ServiceFilterWithParams(removeApplicationInstance), unpublishApplicationService);

			// TODO Get already registered capabilities and apps, and publish them.

		} catch (ServiceNotFoundException e) {
			log.error("Failed to register APIConnector. REST API will NOT be published automatically.", e);
			throw new ApplicationActivationException(e);
		}

		// publishing the core manually
		// FIXME to be removed
		try {
			restApiProvider.publish(rootResourceProvider, IRootResourceProvider.class, "/mqnaas/IRootResourceProvider/");
			restApiProvider.publish(rootResourceAdmin, IRootResourceAdministration.class, "/mqnaas/IRootResourceAdministration/");
			restApiProvider.publish(observationService, IObservationService.class, "/mqnaas/IObservationService/");
			restApiProvider.publish(executionService, IExecutionService.class, "/mqnaas/IExecutionService/");
			restApiProvider.publish(serviceProvider, IServiceProvider.class, "/mqnaas/IServiceProvider/");
			restApiProvider.publish(coreModelCapability, ICoreModelCapability.class, "/mqnaas/ICoreModelCapability");
		} catch (Exception e) {
			log.error("Failed to register core services API.", e);
			throw new ApplicationActivationException(e);

		}

		registerAsOSGiService();
	}

	@Override
	public void deactivate() {

		unregisterAsOSGiService();

		// TODO unpublish all

	}

	// ////////
	// Services
	// ////////

	@Override
	public void publish(CapabilityNode capabilityNode) throws InvalidCapabilityDefinionException {
		for (Class<? extends ICapability> capabClass : capabilityNode.getContent().getCapabilities()) {
			String uri = getPathForApplication(capabilityNode, capabClass, new StringBuffer()).toString();
			log.debug("Publishing API for interface {} of capability {} with path {}", capabClass.getName(), capabilityNode.getContent()
					.getInstance(), uri);
			restApiProvider.publish((ICapability) capabilityNode.getContent().getProxy(), capabClass, uri);
		}
	}

	@Override
	public void unpublish(CapabilityNode capabilityNode) {
		for (Class<? extends ICapability> capabClass : capabilityNode.getContent().getCapabilities()) {
			log.debug("Unpublishing API for interface {} of capability {}", capabClass.getName(), capabilityNode.getContent().getInstance());
			restApiProvider.unpublish((ICapability) capabilityNode.getContent().getProxy(), capabClass);
		}
	}

	@Override
	public void publish(ApplicationInstance applicationInstance) {
		// TODO Auto-generated method stub
	}

	@Override
	public void unpublish(ApplicationInstance applicationInstance) {
		// TODO Auto-generated method stub
	}

	private StringBuffer getPathForResource(ResourceNode resourceNode, StringBuffer alreadyComputedPath) {

		if (resourceNode == null)
			return alreadyComputedPath;

		if (resourceNode.getContent() instanceof IRootResource &&
				(((IRootResource) resourceNode.getContent()).getDescriptor().getSpecification().getType()).equals(Specification.Type.CORE)) {

			alreadyComputedPath.insert(0, "/mqnaas/");
			return alreadyComputedPath;
		}

		// TODO define a way to get user friendly names for resources
		String resourceName = resourceNode.getContent().getId();
		alreadyComputedPath.insert(0, resourceName + "/");
		return getPathForApplication(resourceNode.getParent(), resourceNode.getParentInterface(), alreadyComputedPath);
	}

	private StringBuffer getPathForApplication(ApplicationNode applicationNode, Class<? extends IApplication> interfaze,
			StringBuffer alreadyComputedPath) {

		if (applicationNode == null)
			return alreadyComputedPath;

		// TODO get applicationName from @Path annotation in interfaze
		String applicationName = interfaze.getSimpleName();

		if (applicationNode instanceof CapabilityNode) {

			CapabilityNode capabilityNode = (CapabilityNode) applicationNode;
			alreadyComputedPath.insert(0, applicationName + "/");
			return getPathForResource(capabilityNode.getParent(), alreadyComputedPath);

		} else {

			alreadyComputedPath.insert(0, "/app/" + applicationName + "/");
			return alreadyComputedPath;
		}
	}

	private void registerAsOSGiService() {
		if (FrameworkUtil.getBundle(APIConnector.class) != null) {
			BundleContext context = FrameworkUtil.getBundle(APIConnector.class).getBundleContext();
			if (context != null) {

				osgiRegistration = context.registerService(IAPIConnector.class, this, null);
				try {
					StringBuilder filter = new StringBuilder();
					filter.append("(").append(Constants.OBJECTCLASS).append("=").append(IAPIConnector.class.getName()).append(")");
					context.addServiceListener(this, filter.toString());
				} catch (InvalidSyntaxException e) {
					log.error("Error adding APIConnector as OSGI service listener.", e);
				}

			}
		}
	}

	private void unregisterAsOSGiService() {
		if (osgiRegistration != null) {
			osgiRegistration.unregister();
			osgiRegistration = null;
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {

		if (event.getType() == ServiceEvent.UNREGISTERING && osgiRegistration != null)
			osgiRegistration = null;

	}
}
