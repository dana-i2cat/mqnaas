package org.opennaas.core.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.opennaas.core.annotation.AddsResource;
import org.opennaas.core.annotation.DependingOn;
import org.opennaas.core.annotation.RemovesResource;
import org.opennaas.core.api.IApplication;
import org.opennaas.core.api.IBindingManagement;
import org.opennaas.core.api.ICapability;
import org.opennaas.core.api.IExecutionService;
import org.opennaas.core.api.IResource;
import org.opennaas.core.api.IResourceManagement;
import org.opennaas.core.api.IRootResource;
import org.opennaas.core.api.IService;
import org.opennaas.core.impl.notificationfilter.ResourceMonitoringFilter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class BindingManagement implements IBindingManagement {

	// At the moment, this is the home of the OpenNaaS resource
	private OpenNaaS					openNaaS;

	// Manages the bundle dependency tree and the capabilities each bundle offers

	private CapabilityManagement		capabilityManagement;

	private ApplicationManagement		applicationManagement;

	// Holds the capabilities bound to the given resource
	private List<CapabilityInstance>	boundCapabilities;

	private List<ApplicationInstance>	applications;

	@DependingOn
	private IExecutionService			executionService;

	@DependingOn
	private IResourceManagement			resourceManagement;

	public BindingManagement() {

		// Initialize the OpenNaaS resource to be able to bind upcoming
		// capability implementations to it...
		openNaaS = new OpenNaaS();

		boundCapabilities = new ArrayList<CapabilityInstance>();
		applications = new ArrayList<ApplicationInstance>();

		// Initialize the capability management, which is used to track
		// capability implementations whenever bundles change...
		capabilityManagement = new CapabilityManagement();

		// ...and the application management, where deployed applications are
		// tracked
		applicationManagement = new ApplicationManagement();

		// The inner core services are instantiated directly...
		resourceManagement = new ResourceManagement();
		executionService = new ExecutionService();

		// Do the first binds manually
		bind(openNaaS, new CapabilityInstance(ResourceManagement.class, resourceManagement));
		bind(openNaaS, new CapabilityInstance(ExecutionService.class, executionService));
		bind(openNaaS, new CapabilityInstance(BindingManagement.class, this));

		// Initialize the notifications necessary to track resources dynamically
		executionService.registerObservation(new ResourceMonitoringFilter(AddsResource.class), getService(openNaaS, "resourceAdded"));
		executionService.registerObservation(new ResourceMonitoringFilter(RemovesResource.class), getService(openNaaS, "resourceRemoved"));

		// There are two ways of adding bundles to the system, each of which will be handled
		BundleContext context = getBundleContext();

		// Way 1. If bundles are added or removed at runtime, the following
		// listener reacts (adding the hook before scanning the already loaded
		// ones to make sure we don't miss any).
		context.addBundleListener(new BundleListener() {

			@Override
			public void bundleChanged(BundleEvent event) {
				if (event.getType() == BundleEvent.STARTED) {
					capabilitiesAdded(capabilityManagement.addBundle(event.getBundle()));
					applicationsAdded(applicationManagement.addBundle(event.getBundle()));
				}
			}

		});

		// Now activate the resource, the services get visible...
		resourceManagement.addResource(openNaaS);

		// Way 2. If bundles are already active, add them now
		for (Bundle bundle : context.getBundles()) {
			if (bundle.getState() == Bundle.ACTIVE) {
				capabilitiesAdded(capabilityManagement.addBundle(bundle));
				applicationsAdded(applicationManagement.addBundle(bundle));
			}
		}

	}

	@Override
	public Multimap<Class<? extends ICapability>, IService> getServices(IResource resource) {

		Multimap<Class<? extends ICapability>, IService> services = ArrayListMultimap.create();

		for (CapabilityInstance representation : boundCapabilities) {
			if (representation.getResource().equals(resource) && representation.isResolved()) {
				services.putAll(representation.getServices());
			}
		}

		return services;
	}

	@Override
	public IService getService(IResource resource, String name) {

		for (IService service : getServices(resource).values()) {
			if (service.getName().equals(name)) {
				return service;
			}
		}

		return null;
	}

	@Override
	public void resourceAdded(IResource resource) {
		// Establish matches
		for (Class<? extends ICapability> capabilityClass : capabilityManagement.getAllCapabilityClasses()) {
			if (shouldBeBound(resource, capabilityClass)) {
				bind(resource, new CapabilityInstance(capabilityClass));
			}
		}
	}

	@Override
	public void resourceRemoved(IResource resource) {
		// TODO add unbind logic
	}

	private void applicationsAdded(Collection<Class<? extends IApplication>> applicationClasses) {
		if (applicationClasses.isEmpty())
			return;

		for (Class<? extends IApplication> applicationClass : applicationClasses) {
			ApplicationInstance application = new ApplicationInstance(applicationClass);

			resolve(application);

			applications.add(application);

			if (application.isResolved()) {
				application.getInstance().onDependenciesResolved();
			}
		}

		printAvailableApplications();
	}

	private void capabilitiesAdded(Collection<Class<? extends ICapability>> capabilityClasses) {
		if (capabilityClasses.isEmpty())
			return;

		// Establish matches
		for (IResource resource : resourceManagement.getResources()) {
			for (Class<? extends ICapability> capabilityClass : capabilityClasses) {

				if (shouldBeBound(resource, capabilityClass)) {
					bind(resource, new CapabilityInstance(capabilityClass));
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void capabilitiesRemoved(Collection<Class<? extends ICapability>> capabilityClasses) {
		// TODO add unbind logic
	}

	private static final String	IS_SUPPORTING_METHOD_NAME	= "isSupporting";

	@Override
	public boolean shouldBeBound(IResource resource, Class<? extends ICapability> capabilityClass) {

		boolean shouldBeBound = false;

		List<Class<?>> interfaces = ClassUtils.getAllInterfaces(capabilityClass);
		interfaces.remove(ICapability.class);

		// If their is no interface remaining, there's nothing to bind...
		if (interfaces.isEmpty())
			return shouldBeBound;

		// Now for the process of binding, which for the moment is a very simple
		// implementation: look for a static isSupporting method in the
		// capability and use it to determine the binding
		try {
			Method isSupportingMethod = capabilityClass.getMethod(IS_SUPPORTING_METHOD_NAME, IResource.class);
			shouldBeBound = (Boolean) isSupportingMethod.invoke(null, resource);
		} catch (Exception e1) {
			if (resource instanceof IRootResource) {
				try {
					Method isSupportingMethod = capabilityClass.getMethod(IS_SUPPORTING_METHOD_NAME, IRootResource.class);
					shouldBeBound = (Boolean) isSupportingMethod.invoke(null, resource);
				} catch (Exception e2) {
					// no way to establish bind
					System.out.println("No way of establishing bind with Capability " + capabilityClass.getName() + ". No isSupporting(...) implementation found.");
				}
			}
		}

		// System.out.println(getClass().getSimpleName() + ".shouldBeBound(" + resource + ", " + capabilityClass + "): " + shouldBeBound);

		return shouldBeBound;
	}

	// TODO Add unbind logic and move
	private void bind(IResource resource, CapabilityInstance capabilityInstance) {

		// 0. Avoid double binds: Stupid way
		for (CapabilityInstance boundCapabilityInstance : boundCapabilities) {
			if (capabilityInstance.getClazz().equals(boundCapabilityInstance.getClazz())) {
				if (boundCapabilityInstance.getResource().equals(resource)) {
					System.out.println("ALREADY BOUND: " + resource + ", " + capabilityInstance);
					return;
				}
			}
		}

		// 1. Bind the representation to the resource
		capabilityInstance.bind(resource);

		// 2. Resolve the dependencies using the newly bound capability
		resolve(capabilityInstance);

		// 3. Add the service to the capability to be able to return it when requested
		boundCapabilities.add(capabilityInstance);
	}

	private void resolve(ApplicationInstance newRepresentation) {
		// Resolve capability dependencies
		for (CapabilityInstance representation : boundCapabilities) {

			// b. Resolve the currently added one with those already registered
			if (!newRepresentation.isResolved()) {
				newRepresentation.resolve(representation);
			}
		}
	}

	private void resolve(CapabilityInstance newRepresentation) {
		// Resolve capability dependencies
		for (CapabilityInstance representation : boundCapabilities) {

			// a. Resolve those already registered that depend on the currently added one
			if (!representation.isResolved()) {
				representation.resolve(newRepresentation);
			}

			// b. Resolve the currently added one with those already registered
			if (!newRepresentation.isResolved()) {
				newRepresentation.resolve(representation);
			}
		}

		for (ApplicationInstance representation : applications) {
			if (!representation.isResolved()) {
				representation.resolve(newRepresentation);

				if (representation.isResolved()) {
					representation.getInstance().onDependenciesResolved();
				}
			}

		}
	}

	private BundleContext getBundleContext() {
		return FrameworkUtil.getBundle(getClass()).getBundleContext();
	}

	public void printAvailableApplications() {
		System.out.println("\nAVAILABLE APPLICATIONS -------------------------------------------");

		for (ApplicationInstance representation : applications) {
			System.out.println(representation + " [resolved=" + representation.getResolvedClasses() + ", pending=" + representation
					.getPendingClasses() + "]");
		}

		System.out.println("------------------------------------------------------------------");
	}

	public void printAvailableServices() {

		System.out.println("\nAVAILABLE SERVICES -----------------------------------------------");

		for (IResource resource : resourceManagement.getResources()) {

			System.out.println("Resource " + resource);

			for (CapabilityInstance representation : boundCapabilities) {

				if (!representation.getResource().equals(resource))
					continue;

				System.out.println(representation + " [resolved=" + representation.getResolvedClasses() + ", pending=" + representation
						.getPendingClasses() + "]");

				for (Class<? extends ICapability> capability : representation.getServices().keySet()) {
					System.out.println("  Services of " + capability);

					System.out.print("    ");
					int index = 0;
					for (IService service : representation.getServices().values()) {
						if (index > 0)
							System.out.print(", ");
						System.out.print(service);
						index++;
					}
					System.out.println();
				}
			}

			System.out.println();
		}

		System.out.println("------------------------------------------------------------------");
	}

}
