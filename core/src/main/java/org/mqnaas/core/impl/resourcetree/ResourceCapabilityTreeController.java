package org.mqnaas.core.impl.resourcetree;

/*
 * #%L
 * MQNaaS :: Core
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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.impl.CapabilityInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class ResourceCapabilityTreeController {

	private static final Logger	log	= LoggerFactory.getLogger(ResourceCapabilityTreeController.class);

	public static ResourceNode createResourceNode(IResource resource, CapabilityNode parent, Class<? extends IApplication> parentInterface) {
		if (parent == null)
			return new ResourceNode(resource);

		ResourceNode toAdd = new ResourceNode(resource, parent, parentInterface);
		return addResourceNode(toAdd, parent, parentInterface);
	}

	public static CapabilityNode createCapabilityNode(CapabilityInstance ci, ResourceNode parent) {
		if (parent == null)
			return new CapabilityNode(ci);

		CapabilityNode toAdd = new CapabilityNode(ci);
		return addCapabilityNode(toAdd, parent);
	}

	public static ResourceNode addResourceNode(ResourceNode toAdd, ApplicationNode parent, Class<? extends IApplication> parentInterface) {
		parent.getChildren().add(toAdd);
		toAdd.setParent(parent);
		toAdd.setParentInterface(parentInterface);
		return toAdd;
	}

	public static ResourceNode removeResourceNode(ResourceNode toRemove) {
		if (toRemove.getParent() != null)
			toRemove.getParent().getChildren().remove(toRemove);
		toRemove.setParent(null);
		toRemove.setParentInterface(null);
		return toRemove;
	}

	public static CapabilityNode addCapabilityNode(CapabilityNode toAdd, ResourceNode parent) {
		parent.getChildren().add(toAdd);
		toAdd.setParent(parent);
		return toAdd;
	}

	public static CapabilityNode removeCapabilityNode(CapabilityNode toRemove) {
		if (toRemove.getParent() != null)
			toRemove.getParent().getChildren().remove(toRemove);
		toRemove.setParent(null);
		return toRemove;
	}

	public static CapabilityNode getChidrenWithContent(ResourceNode parent, CapabilityInstance content) {
		for (CapabilityNode child : parent.getChildren()) {
			if (child.getContent().equals(content))
				return child;
		}
		return null;
	}

	public static ResourceNode getChidrenWithContent(ApplicationNode parent, IResource content) {
		for (ResourceNode child : parent.getChildren()) {
			if (child.getContent().equals(content))
				return child;
		}
		return null;
	}

	public static boolean isBound(Class<? extends ICapability> capabilityClass, ResourceNode resourceNode) {
		for (CapabilityNode capabilityNode : resourceNode.getChildren()) {
			if (capabilityNode.getContent().getClazz().equals(capabilityClass))
				return true;
		}
		return false;
	}

	public static boolean canBind(Class<? extends ICapability> capabilityClass, ResourceNode resourceNode) {
		// To be able to bind, any of the interfaces implemented by capabilityClass MUST NOT be already implemented
		// by capabilities already bound to the resource.
		return !doesCapabilityImplementAlreadyProvidedInterfaces(capabilityClass, resourceNode);
	}

	private static boolean doesCapabilityImplementAlreadyProvidedInterfaces(Class<? extends ICapability> capabilityClass, ResourceNode resourceNode) {
		for (Class<?> capabilityIface : capabilityClass.getInterfaces()) {
			for (CapabilityNode capabilityNode : resourceNode.getChildren()) {
				if (Arrays.asList(capabilityNode.getContent().getClazz().getInterfaces()).contains(capabilityIface))
					return true;
			}
		}
		return false;
	}

	/**
	 * Looks for a CapabilityNode with given content in the hierarchy starting at startFrom ResourceNode.
	 * 
	 * @param startFrom
	 * @param content
	 * @return CapabilityNode with given content in the hierarchy starting at startFrom ResourceNode, null if there is no such capability node in this
	 *         hierarchy.
	 */
	public static CapabilityNode getCapabilityNodeWithContent(ResourceNode startFrom, CapabilityInstance content) {

		// search in startFrom node
		CapabilityNode found = getChidrenWithContent(startFrom, content);
		if (found != null)
			return found;

		// recursively search in children resources
		for (CapabilityNode capability : startFrom.getChildren()) {
			for (ResourceNode child : capability.getChildren()) {
				found = getCapabilityNodeWithContent(child, content);
				if (found != null)
					return found;
			}
		}

		// if not found in startFrom capabilities nor in children resources capabilities (if any)
		return null;
	}

	/**
	 * Looks for a CapabilityNode with given content in the hierarchy starting at startFrom ResourceNode.
	 * 
	 * @param startFrom
	 * @param content
	 * @return CapabilityNode with given content in the hierarchy starting at startFrom ResourceNode, null if there is no such capability node in this
	 *         hierarchy.
	 */
	public static ResourceNode getResourceNodeWithContent(ResourceNode startFrom, IResource content) {

		// search in startFrom node
		if (startFrom.getContent().equals(content))
			return startFrom;

		// recursively search in children resources
		ResourceNode found;
		for (CapabilityNode capability : startFrom.getChildren()) {
			for (ResourceNode child : capability.getChildren()) {
				found = getResourceNodeWithContent(child, content);
				if (found != null)
					return found;
			}
		}

		// if not found in startFrom nor in children resources (if any)
		return null;
	}

	/**
	 * Looks for first {@link IRootResource} contained by a {@link ResourceNode} walking up the tree starting from the node containing given
	 * {@link IResource}
	 * 
	 * @param tree
	 *            {@link ResourceCapabilityTree} to be inspected
	 * @param resource
	 *            IResource to start the finding
	 * @return ResourceNode containing the first IRootResource found or null if not found
	 */
	public static ResourceNode getRootResourceNodeFromResource(ResourceCapabilityTree tree, IResource resource) {
		log.debug("Looking for IRootResource associated with this resource: " + resource);

		log.trace("Looking for ResourceNode in the tree containing this resource: " + resource);
		ResourceNode resourceNode = getResourceNodeWithContent(tree.getRootResourceNode(), resource);

		if (resourceNode == null) {
			log.warn("Resource not located in the tree! Resource: " + resource);
			return null;
		}

		while (true) {
			// is root resource?
			if (resourceNode.getContent() instanceof IRootResource) {
				log.trace("IRootResource Resource Node found for resource: " + resourceNode.getContent());
				return resourceNode;
			}

			// get parent resource of capability parent node
			CapabilityNode capabilityNode = ((CapabilityNode) resourceNode.getParent());
			if (capabilityNode == null) {
				// no more nodes, top of the tree
				log.warn("No IRootResource found in the tree for resource: " + resource);
				return null;
			}

			// next node
			resourceNode = capabilityNode.getParent();
		}
	}

	/**
	 * Looks for ResourceNodes in the hierarchy starting at startFrom ResourceNode.
	 * 
	 * @param startFrom
	 * @return
	 */
	public static List<ResourceNode> getAllResourceNodes(ResourceNode startFrom) {

		List<ResourceNode> resources = new LinkedList<ResourceNode>();
		resources.add(startFrom);

		// recursively search in children resources
		for (CapabilityNode capability : startFrom.getChildren()) {
			for (ResourceNode child : capability.getChildren()) {
				resources.addAll(getAllResourceNodes(child));
			}
		}

		return resources;
	}

	/**
	 * Looks for CapabilityNodes in the hierarchy starting at startFrom ResourceNode.
	 * 
	 * @param startFrom
	 * @return
	 */
	public static List<CapabilityNode> getAllCapabilityNodes(ResourceNode startFrom) {

		List<CapabilityNode> capabilities = new LinkedList<CapabilityNode>();
		capabilities.addAll(startFrom.getChildren());

		// recursively search in children resources
		for (CapabilityNode capability : startFrom.getChildren()) {
			for (ResourceNode child : capability.getChildren()) {
				capabilities.addAll(getAllCapabilityNodes(child));
			}
		}

		return capabilities;
	}

	/**
	 * Looks for a CapabilityNode in the hierarchy starting at startFrom ResourceNode, having given contentInstance in content.getInstance()
	 * 
	 * @param startFrom
	 * @param content
	 * @return CapabilityNode with given contentInstance in content.getInstance() in the hierarchy starting at startFrom ResourceNode, null if there
	 *         is no such capability node in this hierarchy.
	 */
	public static CapabilityNode getCapabilityNodeWithContentCapability(ResourceNode startFrom, ICapability contentInstance) {

		// search in startFrom node
		CapabilityNode found = getChidrenWithContentCapability(startFrom, contentInstance);
		if (found != null)
			return found;

		// recursively search in children resources
		for (CapabilityNode capability : startFrom.getChildren()) {
			for (ResourceNode child : capability.getChildren()) {
				found = getCapabilityNodeWithContentCapability(child, contentInstance);
				if (found != null)
					return found;
			}
		}

		// if not found in startFrom capabilities nor in children resources capabilities (if any)
		return null;
	}

	/**
	 * Looks for a child of given parent a CapabilityNode having given contentInstance in CapabilityNode.getContent().getInstance()
	 * 
	 * @param parent
	 * @param contentInstance
	 * @return CapabilityNode with given contentInstance in content.getInstance(), null if there is no such capability in parent.
	 */
	public static CapabilityNode getChidrenWithContentCapability(ResourceNode parent, ICapability contentInstance) {
		for (CapabilityNode child : parent.getChildren()) {
			if (child.getContent().getInstance() == contentInstance)
				return child;
		}
		return null;
	}
}
