package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IBindingDecider;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagement;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.RemovesResource;
import org.mqnaas.core.impl.notificationfilter.ResourceMonitoringFilter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * <p>
 * <code>BindingManagement</code> is one of the core capabilities of MQNaaS and is the capability providing the most basic services, the management of
 * available services based on the available resources (managed by {@link IResourceManagement}) and the available capability implementations (managed
 * by the platform administrator by adding and removing bundles containing implementations).
 * </p>
 * 
 * <p>
 * It's main responsibilities are:
 * 
 * <ol>
 * <li><u>Manage the capabilities available in the platform.</u> This task has two aspects:
 * <ul>
 * <li>Manage the {@link ICapability}s available, e.g. which capability interfaces are defined</li>
 * <li>Manage the {@link ICapability} implementations available, e.g. which classes implement which {@link ICapability}s.
 * </ul>
 * </li>
 * <li><u>Manage the {@link IApplication}s available.</u> An <code>IApplication</code> is third party code requiring utilizing platform services to
 * provide its functionalities.</li>
 * <li><u>Listen to resource being added and removed (see {@link #resourceAdded(IResource)} and {@link #resourceRemoved(IResource)}) for details and
 * update the set of services available depending on available capability implementations and resources.</li>
 * </ol>
 * 
 * <p>
 * Some of these services are not available to the majority of platform users, but are reserved for the sole use of the core, e.g.
 * {@link #resourceAdded(IResource)} and {@link #resourceRemoved(IResource)}.
 * </p>
 */
public class BindingManagement implements IServiceProvider, IResourceManagementListener {

	// At the moment, this is the home of the MQNaaS resource
	private MQNaaS						mqNaaS;

	// Manages the bundle dependency tree and the capabilities each bundle offers

	private CapabilityManagement		capabilityManagement;

	private ApplicationManagement		applicationManagement;

	// Holds the capabilities bound to the given resource
	private List<CapabilityInstance>	boundCapabilities;

	private List<ApplicationInstance>	applications;

	private IExecutionService			executionService;
	private IResourceManagement			resourceManagement;
	private IBindingDecider				bindingDecider;

	public BindingManagement() {

		// Initialize the MQNaaS resource to be able to bind upcoming
		// capability implementations to it...
		mqNaaS = new MQNaaS();

		boundCapabilities = new ArrayList<CapabilityInstance>();
		applications = new ArrayList<ApplicationInstance>();

		// Initialize the capability management, which is used to track
		// capability implementations whenever bundles change...
		capabilityManagement = new CapabilityManagement();

		// ...and the application management, where deployed applications are
		// tracked
		applicationManagement = new ApplicationManagement();

		// The inner core services are instantiated directly...
		// TODO resolve the instances implementing these interfaces using a internal resolving mechanism. they should be resolved before other
		// dependencies resolution
		resourceManagement = new ResourceManagement();
		executionService = new ExecutionService();
		bindingDecider = new BinderDecider();

		// Do the first binds manually
		bind(mqNaaS, new CapabilityInstance(ResourceManagement.class, resourceManagement));
		bind(mqNaaS, new CapabilityInstance(ExecutionService.class, executionService));
		bind(mqNaaS, new CapabilityInstance(BinderDecider.class, bindingDecider));
		bind(mqNaaS, new CapabilityInstance(BindingManagement.class, this));

		// Initialize the notifications necessary to track resources dynamically
		executionService.registerObservation(new ResourceMonitoringFilter(AddsResource.class), getService(mqNaaS, "resourceAdded"));
		executionService.registerObservation(new ResourceMonitoringFilter(RemovesResource.class), getService(mqNaaS, "resourceRemoved"));

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
		resourceManagement.addResource(mqNaaS);

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
			if (service.getMetadata().getName().equals(name)) {
				return service;
			}
		}

		return null;
	}

	/**
	 * <p>
	 * This is the service called by the {@link IResourceManagement} whenever a new {@link IResource} was added to the platform.
	 * </p>
	 * <p>
	 * The {@link BindingManagement} implementation than
	 * <ol>
	 * <li>checks whether the added {@link IResource} can be bound to any of the currently available capability implementations (using
	 * {@link IBindingDecider#shouldBeBound(IResource, Class)}),</li>
	 * <li>binds the {@link IResource} and the capability implementation,</li>
	 * <li>resolves all capability dependencies, and</li>
	 * <li>makes all services available which are defined in <b>all</b> newly resolved capability implementations.</li>
	 * </ol>
	 * 
	 * @param resource
	 *            The resource added to the platform
	 */
	@Override
	public void resourceAdded(IResource resource) {
		// Establish matches
		for (Class<? extends ICapability> capabilityClass : capabilityManagement.getAllCapabilityClasses()) {
			if (bindingDecider.shouldBeBound(resource, capabilityClass)) {
				bind(resource, new CapabilityInstance(capabilityClass));
			}
		}
	}

	/**
	 * <p>
	 * This is the service called by the {@link IResourceManagement} whenever a new {@link IResource} was removed from the platform.
	 * </p>
	 * <p>
	 * The {@link BindingManagement} implementation than
	 * 
	 * <ol>
	 * <li>unbinds the {@link IResource} from all its capability implementations,</li>
	 * <li>unresolves all capability implementation dependencies, and</li>
	 * <li>removes all services which were provided by the given resource.</li>
	 * </ol>
	 * 
	 * @param resource
	 *            The resource removed from the platform
	 */
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

				if (bindingDecider.shouldBeBound(resource, capabilityClass)) {
					bind(resource, new CapabilityInstance(capabilityClass));
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void capabilitiesRemoved(Collection<Class<? extends ICapability>> capabilityClasses) {
		// TODO add unbind logic
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

	@Override
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
