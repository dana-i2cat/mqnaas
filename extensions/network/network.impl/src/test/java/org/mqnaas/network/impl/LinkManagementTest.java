package org.mqnaas.network.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.impl.RootResource;
import org.mqnaas.network.impl.request.RequestResource;
import org.mqnaas.network.impl.topology.link.LinkManagement;

/**
 * <p>
 * Class containing tests for the {@link LinkManagement} capability implementation.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class LinkManagementTest {

	/**
	 * Checks that the {@link LinkManagement#isSupporting(org.mqnaas.core.api.IResource)} method defines:
	 * <ul>
	 * <li>LinkManagement does not support MQNaaS CORE resource.
	 * <li>LinkManagement supports generic networks.</li>
	 * <li>LinkManagement supports NITOS testbed.</li>
	 * <li>LinkManagement does not support no-network {@link IRootResource}s (for example: Router)</li>
	 * <li>LinkManagement supports {@link RequestResource}s</li>
	 * </ul>
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws URISyntaxException
	 */
	@Test
	public void isSupportingTest() throws InstantiationException, IllegalAccessException, URISyntaxException {

		// MQNaaS core resource should not be supported.
		Specification spec = new Specification(Type.CORE);
		Assert.assertFalse("LinkManagement should be not be bound to MQNaaS CORE resource.",
				LinkManagement.isSupporting(new RootResource(RootResourceDescriptor.create(spec))));

		// networks without model should be supported.
		spec = new Specification(Type.NETWORK);
		Assert.assertTrue("LinkManagement should be bound to networks.",
				LinkManagement.isSupporting(new RootResource(RootResourceDescriptor.create(spec))));

		// NITOS network should be supported.
		spec = new Specification(Type.NETWORK, "nitos");
		Assert.assertTrue("LinkManagement should be bound to networks of type NITOS.",
				LinkManagement.isSupporting(new RootResource(RootResourceDescriptor.create(spec))));

		// Router should not be supported.
		spec = new Specification(Type.ROUTER, "junos", "12.10");
		Endpoint endpoint = new Endpoint(new URI("http://localhost:8080/router1"));
		Assert.assertFalse("LinkManagement should not be bound to no-network IRootResources.",
				LinkManagement.isSupporting(new RootResource(RootResourceDescriptor.create(spec, Arrays.asList(endpoint)))));

		// RequestResource should be supported.
		RequestResource requestResource = new RequestResource();
		Assert.assertTrue("LinkManagement should be bound to any RequestResource.",
				LinkManagement.isSupporting(requestResource));

	}
}
