package org.mqnaas.core.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.IBindingDecider;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IExecutionService;
import org.mqnaas.core.api.IObservationService;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IResourceManagementListener;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceManagement;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.core.api.annotations.RemovesResource;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.impl.exceptions.CapabilityInstanceNotFoundException;
import org.mqnaas.core.impl.notificationfilter.ResourceMonitoringFilter;
import org.mqnaas.core.impl.notificationfilter.ServiceFilter;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceCapabilityTree;
import org.mqnaas.core.impl.resourcetree.ResourceCapabilityTreeController;
import org.mqnaas.core.impl.resourcetree.ResourceNode;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
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
 * <li><u>Listen to resource being added and removed (see {@link #resourceCreated(IResource, CapabilityInstance)} and
 * {@link #resourceDestroyed(IResource, CapabilityInstance)}) for details and update the set of services available depending on available capability
 * implementations and resources.</li>
 * </ol>
 * 
 * <p>
 * Some of these services are not available to the majority of platform users, but are reserved for the sole use of the core, e.g.
 * {@link #resourceCreated(IResource, CapabilityInstance)} and {@link #resourceDestroyed(IResource, CapabilityInstance)}.
 * </p>
 */
public class BindingManagement implements IServiceProvider, IResourceManagementListener, IInternalResourceManagementListener, IBindingManagement,
		IBindingManagementEventListener {

	// At the moment, this is the home of the MQNaaS resource
	// private MQNaaS mqNaaS;

	// Holds the capabilities bound to the given resource
	private List<CapabilityInstance>			boundCapabilities;

	private List<ApplicationInstance>			applications;

	// Injected core services
	IExecutionService							executionService;
	IObservationService							observationService;
	IRootResourceManagement						resourceManagement;
	IBindingDecider								bindingDecider;

	// Holds known capability implementations that will be checked for compatibility with resources in the system.
	private Set<Class<? extends ICapability>>	knownCapabilities;
	// Holds known application implementations
	private Set<Class<? extends IApplication>>	knownApplications;

	private ResourceCapabilityTree				tree;

	public BindingManagement() {

		boundCapabilities = new ArrayList<CapabilityInstance>();
		applications = new ArrayList<ApplicationInstance>();

		knownCapabilities = new HashSet<Class<? extends ICapability>>();
		knownApplications = new HashSet<Class<? extends IApplication>>();

	}

	public void init() throws Exception {

		if (executionService == null || observationService == null || resourceManagement == null || bindingDecider == null) {
			throw new Exception("Failed to initialize. Required services not set.");
		}

		// Now activate the resource, the services get visible...
		// Initialize the MQNaaS resource to be able to bind upcoming
		// capability implementations to it...
		IRootResource mqNaaS = resourceManagement.createRootResource(new Specification(Type.CORE));
		ResourceNode mqNaaSNode = ResourceCapabilityTreeController.createResourceNode(mqNaaS, null);

		// initialize the tree
		tree = new ResourceCapabilityTree();
		tree.setRootResourceNode(mqNaaSNode);

		CapabilityInstance resourceManagementCI = new CapabilityInstance(RootResourceManagement.class, resourceManagement);
		CapabilityInstance executionServiceCI = new CapabilityInstance(ExecutionService.class, executionService);
		CapabilityInstance binderDeciderCI = new CapabilityInstance(BinderDecider.class, bindingDecider);
		CapabilityInstance bindingManagementCI = new CapabilityInstance(BindingManagement.class, this);

		// Do the first binds manually
		bind(new CapabilityNode(resourceManagementCI), mqNaaSNode);
		bind(new CapabilityNode(executionServiceCI), mqNaaSNode);
		bind(new CapabilityNode(binderDeciderCI), mqNaaSNode);
		bind(new CapabilityNode(bindingManagementCI), mqNaaSNode);

		// Initialize the notifications necessary to track resources dynamically
		// Register the service {@link IInternalResourceManagementListener#resourceCreated(IResource, CapabilityInstance)}
		// Register the service {@link IInternalResourceManagementListener#resourceDestroyed(IResource, CapabilityInstance)}
		try {
			observationService.registerObservation(new ResourceMonitoringFilter(AddsResource.class),
					getService(mqNaaS, "resourceCreated", IResource.class, CapabilityInstance.class));
			observationService.registerObservation(new ResourceMonitoringFilter(RemovesResource.class),
					getService(mqNaaS, "resourceDestroyed", IResource.class, CapabilityInstance.class));
			// Initialize the notifications allowing extensions to track resources dynamically (using IResourceManagementListener interface)
			// These notifications are executed once the resource has already been processed by this BindingManagement ;)
			observationService.registerObservation(
					new ServiceFilter(getService(mqNaaS, "resourceCreated", IResource.class, CapabilityInstance.class)),
					getService(mqNaaS, "resourceAdded", IResource.class));
			observationService.registerObservation(
					new ServiceFilter(getService(mqNaaS, "resourceDestroyed", IResource.class, CapabilityInstance.class)),
					getService(mqNaaS, "resourceRemoved", IResource.class));

		} catch (ServiceNotFoundException e) {
			// FIXME use logger
			System.out.println("Error registering observation!");
			e.printStackTrace();
		}

	}

	ResourceCapabilityTree getResourceCapabilityTree() {
		return tree;
	}

	public static boolean isSupporting(IRootResource resource) {
		return resource.getSpecification().getType() == Specification.Type.CORE;
	}

	// ///////////////////////////////////////
	// {@link IServiceProvider} implementation
	// ///////////////////////////////////////

	@Override
	public Multimap<Class<? extends IApplication>, IService> getServices(IResource resource) {

		Multimap<Class<? extends IApplication>, IService> services = ArrayListMultimap.create();

		for (CapabilityInstance representation : filterResolved(getCapabilityInstancesBoundToResource(resource))) {
			services.putAll(representation.getServices());
		}

		return services;
	}

	@Override
	public IService getService(IResource resource, String name, Class<?>... parameterClasses) throws ServiceNotFoundException {

		for (IService service : getServices(resource).values()) {
			if (service.getMetadata().getName().equals(name)) {
				if (Arrays.equals(service.getMetadata().getParameterTypes(), parameterClasses)) {
					return service;
				}
			}
		}

		throw new ServiceNotFoundException("Service " + name + " of resource " + resource + " not found.");
	}

	// //////////////////////////////////////////////////
	// {@link IResourceManagementListener} implementation
	// //////////////////////////////////////////////////

	@Override
	public void resourceAdded(IResource resource) {
		// Nothing to do
		// This is just a service exposed to announce that a resource has been added to the system

	}

	@Override
	public void resourceRemoved(IResource resource) {
		// Nothing to do
		// This is just a service exposed to announce that a resource has been removed from the system

	}

	// //////////////////////////////////////////////////
	// {@link IInternalResourceManagementListener} implementation
	// //////////////////////////////////////////////////

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
	 * @param addedTo
	 */
	@Override
	public void resourceCreated(IResource added, CapabilityInstance managedBy) {

		try {
			CapabilityNode parent = ResourceCapabilityTreeController.getCapabilityNodeWithContent(tree.getRootResourceNode(), managedBy);
			if (parent == null)
				throw new CapabilityInstanceNotFoundException(managedBy, "Unknown capability instance");

			addResourceNode(new ResourceNode(added), parent);
		} catch (CapabilityInstanceNotFoundException e) {
			System.err.println(e);
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
	 * @param removedFrom
	 */
	@Override
	public void resourceDestroyed(IResource removed, CapabilityInstance managedBy) {
		try {
			CapabilityNode parent = ResourceCapabilityTreeController.getCapabilityNodeWithContent(tree.getRootResourceNode(), managedBy);
			if (parent == null)
				throw new CapabilityInstanceNotFoundException(managedBy, "Unknown capability instance");

			ResourceNode toRemove = ResourceCapabilityTreeController.getChidrenWithContent(parent, removed);
			if (toRemove == null)
				throw new ResourceNotFoundException("Resource is not provided by given capability instance");

			removeResourceNode(toRemove, parent);
		} catch (CapabilityInstanceNotFoundException e) {
			System.err.println(e);
		} catch (ResourceNotFoundException e) {
			System.err.println(e);
		}
	}

	// /////////////////////////////////////////////////
	// {@link IBindingManagementEventListener} implementation
	// /////////////////////////////////////////////////

	@Override
	public void resourceAdded(ResourceNode added, CapabilityNode managedBy) {
		// Bind matching capabilities
		for (Class<? extends ICapability> capabilityClass : knownCapabilities) {
			if (bindingDecider.shouldBeBound(added.getContent(), capabilityClass)) {
				if (!ResourceCapabilityTreeController.isBound(capabilityClass, added)) {
					bind(new CapabilityNode(new CapabilityInstance(capabilityClass)), added);
				} else {
					System.out.println("Already bound " + capabilityClass + " to resource " + added.getContent());
				}
			}
		}
	}

	@Override
	public void resourceRemoved(ResourceNode removed, CapabilityNode wasManagedBy) {
		// Nothing to do, the resource is already removed
	}

	@Override
	public void capabilityInstanceBound(CapabilityNode bound, ResourceNode boundTo) {
		// revolve bound capability and those depending on it
		resolve(bound.getContent());
	}

	@Override
	public void capabilityInstanceUnbound(CapabilityNode unbound, ResourceNode wasBoundTo) {
		// unresolve unbound capability and those depending on it
		unresolve(unbound.getContent());
	}

	@Override
	public void applicationInstanceAdded(ApplicationInstance added) {
		// resolve application and those depending on it
		resolve(added);
	}

	@Override
	public void applicationInstanceRemoved(ApplicationInstance removed) {
		// unresolve application and those depending on it
		unresolve(removed);
	}

	// /////////////////////////////////////////
	// {@link IBindingManagement} implementation
	// /////////////////////////////////////////

	@Override
	public void addResourceNode(ResourceNode resource, CapabilityNode managedBy) {

		System.out.println("Adding resource " + resource.getContent() + "managed by capability " + managedBy.getContent());

		// CapabilityNode parent = ResourceCapabilityTreeController.getCapabilityNode(managedBy);
		// if (parent == null)
		// throw new CapabilityInstanceNotFoundException(managedBy.getContent(), "Unknown capability instance");

		// 1. Update the model
		ResourceCapabilityTreeController.addResourceNode(resource, managedBy);

		// 2. Notify this class about the addition
		// (it will attempt to bind available capabilities to the new resource)
		resourceAdded(resource, managedBy);
	}

	@Override
	public void removeResourceNode(ResourceNode toRemove, CapabilityNode managedBy) {

		System.out.println("Removing resource " + toRemove.getContent() + "managed by capability " + managedBy.getContent());

		// CapabilityNode parent = ResourceCapabilityTreeController.getCapabilityNode(managedBy);
		// if (parent == null)
		// throw new CapabilityInstanceNotFoundException(managedBy.getContent(), "Unknown capability instance");
		//
		// if (!parent.getChildren().contains(toRemove))
		// throw new ResourceNotFoundException("Resource is not provided by given capability instance");

		// 1. Remove on cascade (remove capabilities bound to this resource)
		// Notice recursivity between removeResource and unbind methods
		for (CapabilityNode bound : toRemove.getChildren()) {
			unbind(bound, toRemove);
		}

		// 2. Update the model
		ResourceCapabilityTreeController.removeResourceNode(toRemove);

		// 3. Notify this class about the resource removal
		resourceRemoved(toRemove, managedBy);
	}

	@Override
	public void bind(CapabilityNode toBind, ResourceNode toBindTo) {

		System.out.println("Binding " + toBind.getContent() + "to resource " + toBindTo.getContent());

		// ResourceNode resourceNode = ResourceCapabilityTreeController.getResourceNode(toBindTo);
		// if (resourceNode == null)
		// throw new ResourceNotFoundException("Unknown resource");

		// 1. Bind the representation to the resource
		toBind.getContent().bind(toBindTo.getContent());

		// 2. Update the model
		boundCapabilities.add(toBind.getContent());
		ResourceCapabilityTreeController.addCapabilityNode(toBind, toBindTo);

		// 3. Announce this class about the binding
		// (it will resolve the newly bound capabilityInstance)
		capabilityInstanceBound(toBind, toBindTo);
	}

	@Override
	public void unbind(CapabilityNode toUnbind, ResourceNode boundTo) {

		System.out.println("Unbinding " + toUnbind.getContent() + "bound to resource " + boundTo.getContent());

		// ResourceNode resourceNode = ResourceCapabilityTreeController.getResourceNode(boundTo);
		// if (resourceNode == null)
		// throw new ResourceNotFoundException("Unknown resource");
		//
		// if (!resourceNode.getChildren().contains(toUnbind))
		// throw new CapabilityInstanceNotFoundException(toUnbind.getContent());

		// 1. Remove on cascade (resources provided by toUnbind capability)
		// Notice recursivity between unbind and removeResource methods
		for (ResourceNode provided : toUnbind.getChildren()) {
			removeResourceNode(provided, toUnbind);
		}

		// 2. Update the model
		ResourceCapabilityTreeController.removeCapabilityNode(toUnbind);
		boundCapabilities.remove(toUnbind.getContent());

		// 3. Announce this class about the unbinding
		// (it will unresolve the unbound capabilityInstance)
		capabilityInstanceUnbound(toUnbind, boundTo);

		// 4. Unbind the representation to the resource
		toUnbind.getContent().unbind();
	}

	@Override
	public void addApplicationInstance(ApplicationInstance applicationInstance) {

		System.out.println("Adding application " + applicationInstance);

		applications.add(applicationInstance);

		applicationInstanceAdded(applicationInstance);
	}

	@Override
	public void removeApplicationInstance(ApplicationInstance applicationInstance) {

		System.out.println("Removing application " + applicationInstance);

		if (applications.remove(applicationInstance))
			applicationInstanceRemoved(applicationInstance);
	}

	// ///////////////////////////
	// Package-protected callbacks
	// ///////////////////////////

	/**
	 * Package-protected callback: Called when application implementations (classes) are available.
	 * 
	 * @param capabilityClasses
	 */
	void applicationsAdded(Collection<Class<? extends IApplication>> applicationClasses) {
		if (applicationClasses.isEmpty())
			return;

		knownApplications.addAll(applicationClasses);

		for (Class<? extends IApplication> applicationClass : applicationClasses) {
			ApplicationInstance application = new ApplicationInstance(applicationClass);

			addApplicationInstance(application);
		}

		printAvailableApplications();
	}

	/**
	 * Package-protected callback: Called when application implementations (classes) are no longer available.
	 * 
	 * @param capabilityClasses
	 */
	void applicationsRemoved(Collection<Class<? extends IApplication>> applicationClasses) {
		if (applicationClasses.isEmpty())
			return;

		knownApplications.removeAll(applicationClasses);

		// remove ApplicationInstances using removed classes
		for (ApplicationInstance application : applications) {
			if (applicationClasses.contains(application.getClazz()))
				removeApplicationInstance(application);
		}

		printAvailableApplications();
	}

	/**
	 * Package-protected callback: Called when new capability implementations (classes) are available.
	 * 
	 * @param capabilityClasses
	 */
	void capabilitiesAdded(Collection<Class<? extends ICapability>> capabilityClasses) {
		if (capabilityClasses.isEmpty())
			return;

		knownCapabilities.addAll(capabilityClasses);

		// Establish matches
		for (ResourceNode resourceNode : ResourceCapabilityTreeController.getAllResourceNodes(tree.getRootResourceNode())) {
			for (Class<? extends ICapability> capabilityClass : capabilityClasses) {
				if (bindingDecider.shouldBeBound(resourceNode.getContent(), capabilityClass)) {
					if (!ResourceCapabilityTreeController.isBound(capabilityClass, resourceNode))
						bind(new CapabilityNode(new CapabilityInstance(capabilityClass)), resourceNode);
				}
			}
		}
	}

	/**
	 * Package-protected callback: Called when capability implementations (classes) are no longer available.
	 * 
	 * @param capabilityClasses
	 */
	void capabilitiesRemoved(Collection<Class<? extends ICapability>> capabilityClasses) {
		if (capabilityClasses.isEmpty())
			return;

		knownCapabilities.removeAll(capabilityClasses);

		// unbind logic
		for (CapabilityNode capabilityNode : ResourceCapabilityTreeController.getAllCapabilityNodes(tree.getRootResourceNode())) {
			if (capabilityClasses.contains(capabilityNode.getContent().getClazz())) {
				unbind(capabilityNode, capabilityNode.getParent());
			}
		}

	}

	// ///////////////
	// private methods
	// ///////////////

	private void resolve(ApplicationInstance newRepresentation) {

		List<ApplicationInstance> resolvedNow = new LinkedList<ApplicationInstance>();
		boolean wasResolved = newRepresentation.isResolved();

		// Resolve capability dependencies
		for (ApplicationInstance representation : getAllCapabilityAndApplicationInstances()) {

			// a. Resolve those already registered that depend on the currently added one
			if (!representation.isResolved()) {
				representation.resolve(newRepresentation);

				if (representation.isResolved()) {
					resolvedNow.add(representation);
				}
			}

			// b. Resolve the currently added one with those already registered
			if (!newRepresentation.isResolved()) {
				newRepresentation.resolve(representation);
			}
		}

		if (!wasResolved && newRepresentation.isResolved()) {
			resolvedNow.add(newRepresentation);
		}

		// c. Notify resolved
		// Notifying AFTER resolving all dependencies that can be resolved with newRepresentation
		// reduces the amount of applications that will be notified when being resolved with unresolved applications.
		for (ApplicationInstance resolved : resolvedNow) {
			resolved.initServices();
			resolved.getInstance().onDependenciesResolved();
		}
	}

	private void unresolve(ApplicationInstance oldRepresentation) {
		// a. Unresolve itself
		boolean resolved = oldRepresentation.isResolved();
		oldRepresentation.unresolveAllDependencies();
		if (resolved) {
			oldRepresentation.stopServices();
			// oldRepresentation.getInstance().onDependenciesUnresolved();
		}

		// b. Unresolve those already registered that depend on the old one
		List<ApplicationInstance> affectedInstances = new LinkedList<ApplicationInstance>();
		for (ApplicationInstance representation : getAllCapabilityAndApplicationInstances()) {
			boolean wasResolved = representation.isResolved();
			boolean affected = representation.unresolve(oldRepresentation);
			if (affected)
				affectedInstances.add(representation);

			// c. Notify unresolved
			if (wasResolved && affected) {
				representation.stopServices();
				// representation.getInstance().onDependenciesUnresolved();
			}
		}

		// d. try to resolve affected ones
		for (ApplicationInstance representation : affectedInstances) {
			resolve(representation);
		}

	}

	List<CapabilityInstance> getAllCapabilityInstances() {
		return boundCapabilities;
	}

	List<ApplicationInstance> getAllCapabilityAndApplicationInstances() {
		List<ApplicationInstance> capabsAndApps = new ArrayList<ApplicationInstance>(applications);
		capabsAndApps.addAll(getAllCapabilityInstances());
		return capabsAndApps;
	}

	List<IResource> getAllResources() {
		List<ResourceNode> allResourceNodes = ResourceCapabilityTreeController.getAllResourceNodes(tree.getRootResourceNode());
		List<IResource> allResources = new ArrayList<IResource>(allResourceNodes.size());
		for (ResourceNode node : allResourceNodes) {
			allResources.add(node.getContent());
		}
		return allResources;
	}

	private Iterable<CapabilityInstance> filterResolved(Iterable<CapabilityInstance> toFilter) {
		Predicate<CapabilityInstance> isResolved = new Predicate<CapabilityInstance>() {
			@Override
			public boolean apply(CapabilityInstance ci) {
				return ci.isResolved();
			}
		};
		return Iterables.filter(toFilter, isResolved);
	}

	List<CapabilityInstance> getCapabilityInstancesBoundToResource(IResource resource) {

		ResourceNode resourceNode = ResourceCapabilityTreeController.getResourceNodeWithContent(tree.getRootResourceNode(), resource);
		if (resourceNode == null)
			return new ArrayList<CapabilityInstance>(0);

		List<CapabilityInstance> bound = new ArrayList<CapabilityInstance>();
		for (CapabilityNode capabNode : resourceNode.getChildren()) {
			bound.add(capabNode.getContent());
		}
		return bound;
	}

	List<IResource> getResourcesProvidedByCapabilityInstance(CapabilityInstance capabilityInstance) {

		CapabilityNode capab = ResourceCapabilityTreeController.getCapabilityNodeWithContent(tree.getRootResourceNode(), capabilityInstance);
		if (capab == null)
			return new ArrayList<IResource>(0);

		List<IResource> resources = new ArrayList<IResource>(capab.getChildren().size());
		for (ResourceNode resourceNode : capab.getChildren()) {
			resources.add(resourceNode.getContent());
		}
		return resources;
	}

	// ////////////////
	// Printing methods
	// ////////////////

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

		for (IResource resource : resourceManagement.getRootResources()) {

			System.out.println("Resource " + resource);

			for (CapabilityInstance representation : getCapabilityInstancesBoundToResource(resource)) {

				System.out.println(representation + " [resolved=" + representation.getResolvedClasses() + ", pending=" + representation
						.getPendingClasses() + "]");

				for (Class<? extends IApplication> capability : representation.getServices().keySet()) {
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

	@Override
	public void onDependenciesResolved() {
		// TODO Auto-generated method stub

	}
}
