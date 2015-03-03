package org.mqnaas.core.api;

/*
 * #%L
 * MQNaaS :: Core.API
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

		// case 2 - core resource

		RootResourceDescriptor coreRDD = RootResourceDescriptor.create(new Specification(Type.CORE));

		Assert.assertTrue("Network RootResourceDescriptor should contain no endpoints.", coreRDD.getEndpoints().isEmpty());
		Assert.assertEquals("RootResourceDescriptor should containn NETWORK as spesification type.", Type.CORE, coreRDD.getSpecification()
				.getType());

	}
}
