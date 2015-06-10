package org.mqnaas.extensions.network.itests;

/*
 * #%L
 * MQNaaS :: Network Integration Tests
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i
 * 			Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.IAttributeStore;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceAdministration;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.IServiceProvider;
import org.mqnaas.core.api.RootResourceDescriptor;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.CapabilityNotFoundException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;
import org.mqnaas.core.api.exceptions.ServiceNotFoundException;
import org.mqnaas.core.api.slicing.Cube;
import org.mqnaas.core.api.slicing.Range;
import org.mqnaas.core.impl.slicing.Slice;
import org.mqnaas.core.impl.slicing.Unit;
import org.mqnaas.network.api.exceptions.NetworkCreationException;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.impl.Link;
import org.mqnaas.network.impl.Network;
import org.mqnaas.network.impl.NetworkManagement;
import org.mqnaas.network.impl.NetworkSubResource;
import org.mqnaas.network.impl.PortResourceWrapper;
import org.mqnaas.network.impl.request.Request;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.KarafDistributionOption;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class NetworkManagementTest {

	@Inject
	IRootResourceAdministration	rootResourceAdmin;

	@Inject
	IRootResourceProvider		rootResourceProvider;

	@Inject
	IServiceProvider			serviceProvider;

	private Network				physicalNetwork;
	private NetworkSubResource	physicalSwitch1;
	private NetworkSubResource	physicalSwitch2;

	private PortResourceWrapper	physicalPort11;
	private PortResourceWrapper	physicalPort12;
	private PortResourceWrapper	physicalPort21;
	private PortResourceWrapper	physicalPort22;

	private final static String	PORT_UNIT_NAME					= "port";
	private final static String	PHYSICAL_PORT_11_EXTERNAL_ID	= "eth0";
	private final static String	PHYSICAL_PORT_12_EXTERNAL_ID	= "eth1";
	private final static String	PHYSICAL_PORT_21_EXTERNAL_ID	= "eth0";
	private final static String	PHYSICAL_PORT_22_EXTERNAL_ID	= "eth1";

	@Configuration
	public Option[] config() {
		// FIXME Read mqnass features version from maven.
		// now mqnaas features version in this file must be changed manually in each release!
		return new Option[] {
				// distribution to test: Karaf 3.0.3
				KarafDistributionOption.karafDistributionConfiguration()
						.frameworkUrl(CoreOptions.maven().groupId("org.apache.karaf").artifactId("apache-karaf").type("tar.gz").version("3.0.3"))
						.karafVersion("3.0.3").name("Apache Karaf").useDeployFolder(false)
						// keep deployed Karaf
						.unpackDirectory(new File("target/paxexam")),
				// no local and remote consoles
				KarafDistributionOption.configureConsole().ignoreLocalConsole(),
				KarafDistributionOption.configureConsole().ignoreRemoteShell(),
				// keep runtime folder allowing analysing results
				KarafDistributionOption.keepRuntimeFolder(),
				// use custom logging configuration file with a custom appender
				KarafDistributionOption.replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg", new File(
						"src/test/resources/org.ops4j.pax.logging.cfg")),
				// maintain our log configuration
				KarafDistributionOption.doNotModifyLogConfiguration(),
				// add network feature
				KarafDistributionOption.features(CoreOptions.maven().groupId("org.mqnaas.extensions").artifactId("network").classifier("features")
						.type("xml").version("0.0.1-SNAPSHOT"), "network"),
				CoreOptions.mavenBundle("org.mqnaas.extensions", "network-test-helpers", "0.0.1-SNAPSHOT"),
		// debug option
		// KarafDistributionOption.debugConfiguration()
		};
	}

	@Before
	public void prepareTest() throws InstantiationException, IllegalAccessException, URISyntaxException, CapabilityNotFoundException,
			ServiceNotFoundException, InterruptedException {

		Endpoint endpoint1 = new Endpoint(new URI("http://www.myfakeresource.com/ofswitch1"));
		Endpoint endpoint2 = new Endpoint(new URI("http://www.myfakeresource.com/ofswitch2"));

		// create physical network
		IRootResource networkResource = rootResourceAdmin.createRootResource(RootResourceDescriptor.create(new Specification(Type.NETWORK)));
		physicalNetwork = new Network(networkResource, serviceProvider);

		// create 2 of_switches in the physical network
		IRootResource ofSwitchResource1 = physicalNetwork.createResource(new Specification(Type.OF_SWITCH), Arrays.asList(endpoint1));
		IRootResource ofSwitchResource2 = physicalNetwork.createResource(new Specification(Type.OF_SWITCH), Arrays.asList(endpoint2));
		physicalSwitch1 = new NetworkSubResource(ofSwitchResource1, serviceProvider);
		physicalSwitch2 = new NetworkSubResource(ofSwitchResource2, serviceProvider);

		// create ports in switches

		physicalPort11 = new PortResourceWrapper(physicalSwitch1.createPort(), serviceProvider);
		physicalPort12 = new PortResourceWrapper(physicalSwitch1.createPort(), serviceProvider);
		physicalPort21 = new PortResourceWrapper(physicalSwitch2.createPort(), serviceProvider);
		physicalPort22 = new PortResourceWrapper(physicalSwitch2.createPort(), serviceProvider);

		// map ports to external ids
		physicalPort11.setAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE, PHYSICAL_PORT_11_EXTERNAL_ID);
		physicalPort12.setAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE, PHYSICAL_PORT_12_EXTERNAL_ID);
		physicalPort21.setAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE, PHYSICAL_PORT_21_EXTERNAL_ID);
		physicalPort22.setAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE, PHYSICAL_PORT_22_EXTERNAL_ID);

		// create link between switches
		Link link = new Link(physicalNetwork.createLink(), serviceProvider);
		link.setSrcPort(physicalPort12.getPortResource());
		link.setDstPort(physicalPort21.getPortResource());

		// create slice unit port with range 0-1
		Slice physicalSwitch1Slice = new Slice(physicalSwitch1.getSlice(), serviceProvider);
		Slice physicalSwitch2Slice = new Slice(physicalSwitch2.getSlice(), serviceProvider);
		Unit portUnitSwitch1 = physicalSwitch1Slice.addUnit(PORT_UNIT_NAME);
		Unit portUnitSwitch2 = physicalSwitch2Slice.addUnit(PORT_UNIT_NAME);
		portUnitSwitch1.setRange(new Range(0, 1));
		portUnitSwitch2.setRange(new Range(0, 1));

		// initialize cubes for unit port
		Range[] range = new Range[1];
		range[0] = new Range(0, 1);
		Cube cube = new Cube(range);
		physicalSwitch1Slice.setCubes(Arrays.asList(cube));
		physicalSwitch2Slice.setCubes(Arrays.asList(cube));

	}

	@Test
	public void createNetworkTest() throws NetworkCreationException, ResourceNotFoundException, CapabilityNotFoundException {

		// create request
		Request request = new Request(physicalNetwork.createRequest(), serviceProvider);

		// create virtual switches
		NetworkSubResource virtualSwitch1 = new NetworkSubResource(request.createResource(Type.OF_SWITCH), serviceProvider);
		NetworkSubResource virtualSwitch2 = new NetworkSubResource(request.createResource(Type.OF_SWITCH), serviceProvider);

		// define mapping between physical and virtual resources
		request.defineMapping(virtualSwitch1.getResource(), physicalSwitch1.getResource());
		request.defineMapping(virtualSwitch2.getResource(), physicalSwitch2.getResource());

		// create ports in switches (they will only contain 1)
		IResource virtualPort1 = virtualSwitch1.createPort();
		IResource virtualPort2 = virtualSwitch2.createPort();

		// define mapping between physical and virtual ports
		request.defineMapping(virtualPort1, physicalPort12.getPortResource());
		request.defineMapping(virtualPort2, physicalPort21.getPortResource());

		// define link between both ports
		Link virtualLink = new Link(request.createLink(), serviceProvider);
		virtualLink.setSrcPort(virtualPort1);
		virtualLink.setDstPort(virtualPort2);

		// create port slice units in both switches
		Slice switch1Slice = new Slice(virtualSwitch1.getSlice(), serviceProvider);
		Unit portUnitSlice1 = switch1Slice.addUnit(PORT_UNIT_NAME);
		portUnitSlice1.setRange(new Range(0, 1));

		Slice switch2Slice = new Slice(virtualSwitch2.getSlice(), serviceProvider);
		Unit portUnitSlice2 = switch2Slice.addUnit(PORT_UNIT_NAME);
		portUnitSlice2.setRange(new Range(0, 1));

		// initialize slice cubes with one port in both switches
		Range[] switch1Range = new Range[1];
		switch1Range[0] = new Range(1, 1);
		Cube cube = new Cube(switch1Range);
		switch1Slice.setCubes(Arrays.asList(cube));

		Range[] switch2Range = new Range[1];
		switch2Range[0] = new Range(0, 0);
		Cube range2Cube = new Cube(switch2Range);
		switch2Slice.setCubes(Arrays.asList(range2Cube));

		// set period
		request.setPeriod(new Period(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())));

		// LAUNCH REQUEST!
		IRootResource createdNetworkResource = physicalNetwork.createVirtualNetwork(request.getRequestResource());
		Network createdNetwork = new Network(createdNetworkResource, serviceProvider);

		// assert network contains two resources
		Assert.assertFalse("Created network should contain two virtual resources.", createdNetwork.getRootResources().isEmpty());
		Assert.assertEquals("Created network should contain two virtual resources.", 2, createdNetwork.getRootResources().size());
		Assert.assertEquals("Created network should contain two virtual of switches.", 2,
				createdNetwork.getRootResources(Type.OF_SWITCH, "virtual", null).size());

		NetworkSubResource createdOfSwitch1 = new NetworkSubResource(createdNetwork.getRootResources().get(0), serviceProvider);
		NetworkSubResource createdOfSwitch2 = new NetworkSubResource(createdNetwork.getRootResources().get(1), serviceProvider);

		// assert both created switches contain 1 port
		Assert.assertFalse("Both created switches should contain 1 port.", createdOfSwitch1.getPorts().isEmpty());
		Assert.assertFalse("Both created switches should contain 1 port.", createdOfSwitch2.getPorts().isEmpty());
		Assert.assertEquals("Both creates switches should contain 1 port.", 1, createdOfSwitch1.getPorts().size());
		Assert.assertEquals("Both creates switches should contain 1 port.", 1, createdOfSwitch2.getPorts().size());

		// check RESOURCE_EXTERNAL_ID values in AttributeStore
		Assert.assertEquals("RESOURCE_EXTERNAL_ID should contain UNKNOWN value.", IAttributeStore.UNKNOWN_VALUE,
				getAttributeStore(createdOfSwitch1.getResource()).getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));
		Assert.assertEquals("RESOURCE_EXTERNAL_ID should contain UNKNOWN value.", IAttributeStore.UNKNOWN_VALUE,
				getAttributeStore(createdOfSwitch2.getResource()).getAttribute(IAttributeStore.RESOURCE_EXTERNAL_ID));

		PortResourceWrapper createdOfSwitch1Port = new PortResourceWrapper(createdOfSwitch1.getPorts().get(0), serviceProvider);
		PortResourceWrapper createdOfSwitch2Port = new PortResourceWrapper(createdOfSwitch2.getPorts().get(0), serviceProvider);

		// assert both ports contain the right external id
		Assert.assertEquals("Virtual port of virtual switch 1 should be mapped to external port" + PHYSICAL_PORT_12_EXTERNAL_ID,
				PHYSICAL_PORT_12_EXTERNAL_ID, createdOfSwitch1Port.getAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE));
		Assert.assertEquals("Virtual port of virtual switch 2 should be mapped to external port" + PHYSICAL_PORT_21_EXTERNAL_ID,
				PHYSICAL_PORT_21_EXTERNAL_ID, createdOfSwitch2Port.getAttribute(NetworkManagement.PORT_INTERNAL_ID_ATTRIBUTE));

		// assert network contains one link between the ports of the switches

		Assert.assertFalse("Created network should contain one link.", createdNetwork.getLinks().isEmpty());
		Assert.assertEquals("Created network should contain one link.", 1, createdNetwork.getLinks().size());

		Link link = new Link(createdNetwork.getLinks().get(0), serviceProvider);

		Assert.assertTrue("Virtual port of virtual switch1 should be source or destination port of the virtual network link.", createdOfSwitch1Port
				.getPortResource().equals(link.getSrcPort()) || createdOfSwitch1Port.getPortResource().equals(link.getDstPort()));
		Assert.assertTrue("Virtual port of virtual switch2 should be source or destination port of the virtual network link.", createdOfSwitch2Port
				.getPortResource().equals(link.getSrcPort()) || createdOfSwitch2Port.getPortResource().equals(link.getDstPort()));
		Assert.assertFalse("Link source and destination ports should not be the same.", link.getSrcPort().equals(link.getDstPort()));

		// assert slices
		Slice createdOfswitch1Slice = new Slice(createdOfSwitch1.getSlice(), serviceProvider);
		Slice createdOfswitch2Slice = new Slice(createdOfSwitch2.getSlice(), serviceProvider);

		Assert.assertFalse("Slice of virtual switch 1 should contain one unit.", createdOfswitch1Slice.getUnits().isEmpty());
		Assert.assertEquals("Slice of virtual switch 1 should contain one unit.", 1, createdOfswitch1Slice.getUnits().size());
		Assert.assertEquals("Slice of virtual switch 1 should contain port unit.", PORT_UNIT_NAME, createdOfswitch1Slice.getUnits().get(0).getName());
		Assert.assertEquals("Slice cube of virtual switch 1 should contain port 1", "OX", createdOfswitch1Slice.toMatrix());

		Assert.assertFalse("Slice of virtual switch 2 should contain one unit.", createdOfswitch2Slice.getUnits().isEmpty());
		Assert.assertEquals("Slice of virtual switch 2 should contain one unit.", 1, createdOfswitch2Slice.getUnits().size());
		Assert.assertEquals("Slice of virtual switch 2 should contain port unit.", PORT_UNIT_NAME, createdOfswitch2Slice.getUnits().get(0).getName());
		Assert.assertEquals("Slice cube of virtual switch 2 should contain port 0", "XO", createdOfswitch2Slice.toMatrix());

	}

	private IAttributeStore getAttributeStore(IResource resource) throws CapabilityNotFoundException {
		return serviceProvider.getCapability(resource, IAttributeStore.class);
	}
}
