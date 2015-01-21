package org.mqnaas.core.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mqnaas.core.api.Specification.Type;

/**
 * <p>
 * Class containing tests for {@link RootResourceDescriptor} creation.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class RootResourceDescriptorTest {

	/**
	 * Test checks RootResourceDescriptor requires an {@link Specification}
	 */
	@Test(expected = NullPointerException.class)
	public void nullSpecificationTest() {
		RootResourceDescriptor.create(null);
	}

	/**
	 * Test checks RootResourceDescriptor requires a resource type in {@link Specification}
	 */
	@Test(expected = NullPointerException.class)
	public void nullResourceTypeTest() {
		RootResourceDescriptor.create(new Specification(null));
	}

	/**
	 * Test checks RootResourceDescriptor requires an endpoint for no-network resources.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void nullEndpointTest() {
		RootResourceDescriptor.create(new Specification(Type.ROUTER));
	}

	/**
	 * Test checks
	 * <ul>
	 * <li>
	 * 1) RootResourceDescriptor is created with same Type and endpoints passed as arguments.</li>
	 * <li>2) RootResourceDescriptor is successfully created without requiring endpoint if the resource is of type network.</li>
	 * </ul>
	 */
	@Test
	public void rootResourceDescriptorTest() throws URISyntaxException {

		// case 1 - no-network resource

		Specification specification = new Specification(Type.CPE);
		Endpoint endpoint1 = new Endpoint(new URI("http://www.myfakeendpoint/cpe1"));
		Endpoint endpoint2 = new Endpoint(new URI("http://www.myfakeendpoint/cpe2"));

		RootResourceDescriptor rrd = RootResourceDescriptor.create(specification, Arrays.asList(endpoint1, endpoint2));

		Assert.assertEquals("RootResourceDescriptor should contain 2 endpoints.", 2, rrd.getEndpoints().size());
		Assert.assertTrue("RootResourceDescrpitor should contain both created endpoints.", rrd.getEndpoints().contains(endpoint1) && rrd
				.getEndpoints().contains(endpoint2));

		Assert.assertEquals("RootResourceDescriptor should containn CPE as spesification type.", Type.CPE, rrd.getSpecification().getType());

		// case 2 - network resource

		RootResourceDescriptor netRRD = RootResourceDescriptor.create(new Specification(Type.NETWORK));

		Assert.assertTrue("Network RootResourceDescriptor should contain no endpoints.", netRRD.getEndpoints().isEmpty());
		Assert.assertEquals("RootResourceDescriptor should containn NETWORK as spesification type.", Type.NETWORK, netRRD.getSpecification()
				.getType());

	}
}
