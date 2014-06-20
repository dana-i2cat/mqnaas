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
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.impl.notificationfilter.ResourceMonitoringFilter;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceCapabilityTree;
import org.mqnaas.core.impl.resourcetree.ResourceCapabilityTreeController;
import org.mqnaas.core.impl.resourcetree.ResourceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <li><u>Listen to resource being added and removed (see {@link #resourceAdded(IResource, ICapability)} and
 * {@link #resourceRemoved(IResource, ICapability)}) for details and update the set of services available depending on available capability
 * implementations and resources.</li>
 * </ol>
 * 
 * <p>
 * Some of these services are not available to the majority of platform users, but are reserved for the sole use of the core, e.g.
 * {@link #resourceCreated(IResource, CapabilityInstance)} and {@link #resourceDestroyed(IResource, CapabilityInstance)}.
 * </p>
 */
public class BindingManagement implements IServiceProvider, IResourceManagementListener, IBindingManagement,
		IBindingManagementEventListener {

	private static final Logger					log	= LoggerFactory.getLogger(BindingManagement.class);

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
		// Register the service {@link IResourceManagementListener#resourceAdded(IResource, ICapability);}
		// Register the service {@link IResourceManagementListener#resourceRemoved(IResource, ICapability);}
		try {
			// TODO Ensure these observations are treated BEFORE any other observation of resource creation/removal.
			// By now, applications willing to react to resource creation or removal should observe services in IResourceManagementListener.
			// They should not use ResourceMonitoringFilter, as the resource may not be ready to be used.
			observationService.registerObservation(new ResourceMonitoringFilter(AddsResource.class),
					getService(mqNaaS, "resourceAdded", IResource.class, ICapability.class));
			observationService.registerObservation(new ResourceMonitoringFilter(RemovesResource.class),
					getService(mqNaaS, "resourceRemoved", IResource.class, ICapability.class));
		} catch (ServiceNotFoundException e) {
			log.error("Error registering observation!", e);
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
	public Multimap<Class<? extends ICapability>, IService> getServices(IResource resource) {

		Multimap<Class<? extends ICapability>, IService> services = ArrayListMultimap.create();

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
	 * @param managedBy
	 *            The ICapability managing given resource
	 */
	@Override
	public void resourceAdded(IResource resource, ICapability managedBy) {
		try {
			CapabilityNode parent = ResourceCapabilityTreeController.getCapabilityNodeWithContentCapability(tree.getRootResourceNode(), managedBy);
			if (parent == null)
				throw new CapabilityNotFoundException(managedBy, "Unknown capability");

			addResourceNode(new ResourceNode(resource), parent);
		} catch (CapabilityNotFoundException e) {
			log.error("No parent found!", e);
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
	 * @param managedBy
	 *            The ICapability managing given resource
	 */
	@Override
	public void resourceRemoved(IResource resource, ICapability managedBy) {
		try {
			CapabilityNode parent = ResourceCapabilityTreeController.getCapabilityNodeWithContentCapability(tree.getRootResourceNode(), managedBy);
			if (parent == null)
				throw new CapabilityNotFoundException(managedBy, "Unknown capability");

			ResourceNode toRemove = ResourceCapabilityTreeController.getChidrenWithContent(parent, resource);
			if (toRemove == null)
				throw new ResourceNotFoundException("Resource is not provided by given capability");

			removeResourceNode(toRemove, parent);
		} catch (CapabilityNotFoundException e) {
			log.error("No parent found!", e);
		} catch (ResourceNotFoundException e) {
			log.error("No resource to be removed found!", e);
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
					log.info("Already bound " + capabilityClass + " to resource " + added.getContent());
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
		// resolve application
		resolve(added);
	}

	@Override
	public void applicationInstanceRemoved(ApplicationInstance removed) {
		// unresolve application
		unresolve(removed);
	}

	// /////////////////////////////////////////
	// {@link IBindingManagement} implementation
	// /////////////////////////////////////////

	@Override
	public void addResourceNode(ResourceNode resource, CapabilityNode managedBy) {

		log.info("Adding resource " + resource.getContent() + " managed by capability " + managedBy.getContent());

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

		log.info("Removing resource " + toRemove.getContent() + " managed by capability " + managedBy.getContent());

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

		log.info("Binding " + toBind.getContent() + " to resource " + toBindTo.getContent());

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

		log.info("Unbinding " + toUnbind.getContent() + " bound to resource " + boundTo.getContent());

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

		applications.add(applicationInstance);

		applicationInstanceAdded(applicationInstance);
	}

	@Override
	public void removeApplicationInstance(ApplicationInstance applicationInstance) {
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
		// Resolve capability dependencies
		for (CapabilityInstance representation : getAllCapabilityInstances()) {

			// a. Resolve those already registered that depend on the currently added one
			// No dependency on ApplicationInstances is allowed by now

			// b. Resolve the currently added one with those already registered
			if (!newRepresentation.isResolved()) {
				newRepresentation.resolve(representation);

			}
		}

		if (newRepresentation.isResolved()) {
			newRepresentation.getInstance().onDependenciesResolved();
		}
	}

	private void resolve(CapabilityInstance newRepresentation) {
		// Resolve capability dependencies
		for (CapabilityInstance representation : getAllCapabilityInstances()) {

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

	private void unresolve(CapabilityInstance oldRepresentation) {

		// a. Unresolve itself (dependencies will resolve (try to) if oldRepresentation is bound again)
		oldRepresentation.unresolveAllDependencies();

		// b. Unresolve those already registered that depend on the old one
		List<CapabilityInstance> affectedCapabilityInstances = new LinkedList<CapabilityInstance>();
		for (CapabilityInstance representation : getAllCapabilityInstances()) {
			boolean affected = representation.unresolve(oldRepresentation);
			if (affected)
				affectedCapabilityInstances.add(representation);
		}

		List<ApplicationInstance> affectedApplicationInstances = new LinkedList<ApplicationInstance>();
		for (ApplicationInstance representation : applications) {
			boolean affected = representation.unresolve(oldRepresentation);
			if (affected)
				affectedApplicationInstances.add(representation);
		}

		// c. try to resolve affected ones
		for (CapabilityInstance representation : affectedCapabilityInstances) {
			resolve(representation);
		}

		for (ApplicationInstance representation : affectedApplicationInstances) {
			resolve(representation);
		}

	}

	private void unresolve(ApplicationInstance oldRepresentation) {

		// a. Unresolve itself (dependencies will resolve (try to) if oldRepresentation is bound again)
		oldRepresentation.unresolveAllDependencies();

		// b. Unresolve those already registered that depend on the old one
		// No dependency on ApplicationInstances is allowed by now
	}

	List<CapabilityInstance> getAllCapabilityInstances() {
		return boundCapabilities;
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
		StringBuffer sb = new StringBuffer();

		sb.append("\nAVAILABLE APPLICATIONS -------------------------------------------\n");

		for (ApplicationInstance representation : applications) {
			sb.append(representation + " [resolved=" + representation.getResolvedClasses() + ", pending=" + representation
					.getPendingClasses() + "]\n");
		}

		sb.append("------------------------------------------------------------------\n");

		log.info(sb.toString());
		System.out.println(sb.toString());
	}

	@Override
	public void printAvailableServices() {
		StringBuffer sb = new StringBuffer();

		sb.append("\nAVAILABLE SERVICES -----------------------------------------------\n");

		for (IResource resource : resourceManagement.getRootResources()) {

			sb.append("Resource " + resource + "\n");

			for (CapabilityInstance representation : getCapabilityInstancesBoundToResource(resource)) {

				sb.append(representation + " [resolved=" + representation.getResolvedClasses() + ", pending=" + representation
						.getPendingClasses() + "]\n");

				for (Class<? extends ICapability> capability : representation.getServices().keySet()) {
					sb.append("  Services of " + capability + "\n");

					sb.append("    ");
					int index = 0;
					for (IService service : representation.getServices().values()) {
						if (index > 0)
							sb.append(", ");
						sb.append(service);
						index++;
					}
					sb.append("\n");
				}
			}

			sb.append("\n");
		}

		sb.append("------------------------------------------------------------------\n");

		log.info(sb.toString());
		System.out.println(sb.toString());
	}
}
