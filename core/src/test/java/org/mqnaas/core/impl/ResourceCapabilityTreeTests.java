package org.mqnaas.core.impl;

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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.ILockingBehaviour;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.ITransactionBehavior;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.impl.resourcetree.CapabilityNode;
import org.mqnaas.core.impl.resourcetree.ResourceCapabilityTree;
import org.mqnaas.core.impl.resourcetree.ResourceCapabilityTreeController;
import org.mqnaas.core.impl.resourcetree.ResourceNode;

/**
 * Unit Tests of {@link ResourceCapabilityTree} and {@link ResourceCapabilityTreeController}
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class ResourceCapabilityTreeTests {

	// 1 core root resource node
	private ResourceNode	coreResourceNode	= generateCoreRootResourceNode();

	// 3 empty capabilities
	private CapabilityNode	capabilityNodeA		= generateCapabilityNode();
	private CapabilityNode	capabilityNodeB		= generateCapabilityNode();
	private CapabilityNode	capabilityNodeC		= generateCapabilityNode();

	// 3 root resource nodes
	private ResourceNode	rootResourceNodeA	= generateRouterRootResourceNode();
	private ResourceNode	rootResourceNodeB	= generateRouterRootResourceNode();
	private ResourceNode	rootResourceNodeC	= generateRouterRootResourceNode();

	// 1 capability node
	private CapabilityNode	capabilityNodeAA	= generateCapabilityNode();

	// 1 resource node
	private ResourceNode	resourceNodeA		= generateResourceNode();

	@Test
	public void testGetRootResourceNodeFromResource() {
		ResourceCapabilityTree tree = generateTree();

		ResourceNode resourceNode = ResourceCapabilityTreeController.getRootResourceNodeFromResource(tree, resourceNodeA.getContent());
		Assert.assertEquals("Found resource must be resourceNodeA", rootResourceNodeA, resourceNode);
	}

	/**
	 * Retrieves a generated {@link ResourceCapabilityTree} of this form:
	 * 
	 * <pre>
	 *        |== capability A == | == Root Res A == | == capability AA == | == Res A
	 *        |                   |
	 * CORE ==|== capability B    | == Root Res B
	 *        |                   |
	 *        |== capability C    | == Root Res C
	 * </pre>
	 */
	private ResourceCapabilityTree generateTree() {
		ResourceCapabilityTree tree = new ResourceCapabilityTree();

		// 1 core root resource node
		tree.setRootResourceNode(coreResourceNode);

		// 3 empty child capabilities of coreResourceNode
		ResourceCapabilityTreeController.addCapabilityNode(capabilityNodeA, coreResourceNode);
		ResourceCapabilityTreeController.addCapabilityNode(capabilityNodeB, coreResourceNode);
		ResourceCapabilityTreeController.addCapabilityNode(capabilityNodeC, coreResourceNode);

		// 3 child root resource nodes of capabilityNodeA
		ResourceCapabilityTreeController.addResourceNode(rootResourceNodeA, capabilityNodeA, ICapability.class);
		ResourceCapabilityTreeController.addResourceNode(rootResourceNodeB, capabilityNodeA, ICapability.class);
		ResourceCapabilityTreeController.addResourceNode(rootResourceNodeC, capabilityNodeA, ICapability.class);

		// 1 capability child node of rootResourceNodeA
		ResourceCapabilityTreeController.addCapabilityNode(capabilityNodeAA, rootResourceNodeA);

		// 1 resource child node of capabilityNodeAA
		ResourceCapabilityTreeController.addResourceNode(resourceNodeA, capabilityNodeAA, ICapability.class);

		return tree;
	}

	private static ResourceNode generateCoreRootResourceNode() {
		ResourceNode node = new ResourceNode();
		node.setContent(generateRootResource(new Specification(Type.CORE, "MQNaaS Core", "0.0.1"), null));
		return node;
	}

	private static ResourceNode generateRouterRootResourceNode() {
		ResourceNode node = new ResourceNode();

		Endpoint ep = new Endpoint();
		try {
			ep.setUri(new URI("http://www.i2cat.net/"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		node.setContent(generateRootResource(new Specification(Type.ROUTER, "i2cat router", "dummy"), Arrays.asList(ep)));

		return node;
	}

	private static IRootResource generateRootResource(final Specification specification, final Collection<Endpoint> endpoints) {
		return new IRootResource() {

			RootResourceDescriptor	descriptor	= RootResourceDescriptor.create(specification, endpoints);

			@Override
			public RootResourceDescriptor getDescriptor() {
				return descriptor;
			}

			@Override
			public ITransactionBehavior getTransactionBehaviour() {
				return null;
			}

			@Override
			public ILockingBehaviour getLockingBehaviour() {
				return null;
			}

			@Override
			public String getId() {
				Specification spec = descriptor.getSpecification();
				return spec.getType() + ":" + spec.getModel() + ":" + spec.getVersion();
			}
		};
	}

	private static ResourceNode generateResourceNode() {
		ResourceNode node = new ResourceNode();
		node.setContent(new IResource() {

			@Override
			public String getId() {
				return "auto-generated-resource-id";
			}
		});
		return node;
	}

	private static CapabilityNode generateCapabilityNode() {
		CapabilityNode node = new CapabilityNode();
		node.setContent(new CapabilityInstance((new ICapability() {
			@Override
			public void activate() {
			}

			@Override
			public void deactivate() {
			}
		}).getClass()));
		return node;
	}

}