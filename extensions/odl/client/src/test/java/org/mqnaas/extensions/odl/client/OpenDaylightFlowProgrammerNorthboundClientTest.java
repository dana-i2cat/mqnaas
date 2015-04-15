package org.mqnaas.extensions.odl.client;

/*
 * #%L
 * MQNaaS :: ODL Client
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

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mqnaas.extensions.odl.client.helium.flowprogrammer.api.IOpenDaylightFlowProgrammerNorthbound;
import org.mqnaas.extensions.odl.helium.flowprogrammer.model.FlowConfig;
import org.mqnaas.extensions.odl.helium.flowprogrammer.model.FlowConfigs;
import org.mqnaas.extensions.odl.helium.flowprogrammer.model.Node;



public class OpenDaylightFlowProgrammerNorthboundClientTest {
	
	private static final String URI = "http://dev.ofertie.i2cat.net:8080";
	private static final String USR = "admin";
	private static final String PWD = "admin";
	
	private static final String DPID = "00:00:00:00:00:00:00:01";
	private static final String SAMPLE_FLOW_NAME = "testFlow1";
	
	private static IOpenDaylightFlowProgrammerNorthbound client;
	
	@BeforeClass
	public static void initClient() {
		client = new OpenDaylightClientFactory().createClient(URI, USR, PWD);
	}
	
	@Before
	public void addSampleFlow() throws Exception {
		FlowConfig flow = sampleFlow("OUTPUT=2");
		try {
			client.addOrModifyFlow(flow, DPID, flow.getName());
		} catch (Exception e) {
			// just to see it in the console
			System.err.println("[OpenDaylightFlowProgrammerClientTest] Error adding sample flow flow: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	@After
	public void deleteSampleFlow() throws Exception {
		FlowConfig flow = sampleFlow("OUTPUT=2");
		try {
			client.deleteFlow(DPID, flow.getName());
		} catch (Exception e) {
			// just to see it in the console
			System.err.println("[OpenDaylightFlowProgrammerClientTest] Error deleting sample flow: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
	
	@Test
	public void getAllFlowsTest() throws Exception {
		FlowConfigs allFlows = client.getStaticFlows();
		Assert.assertNotNull(allFlows);
		
		Assert.assertTrue(allFlows.getFlowConfig().contains(sampleFlow("OUTPUT=2")));
	}
	
	@Test
	public void getFlowsInSwitchTest() throws Exception {
		FlowConfigs allFlows = client.getStaticFlows(DPID);
		Assert.assertNotNull(allFlows);
		
		Assert.assertTrue(allFlows.getFlowConfig().contains(sampleFlow("OUTPUT=2")));
	}
	
	@Test
	public void getFlowTest() throws Exception {
		FlowConfig flow = client.getStaticFlow(DPID, SAMPLE_FLOW_NAME);
		Assert.assertNotNull(flow);
		Assert.assertEquals(flow, sampleFlow("OUTPUT=2"));
	}
	
	@Test
	public void putAndDeleteDropFlowTest() throws Exception {
		String flowName = "testPutAndDeleteDrop";
		
		FlowConfig flow = sampleFlow("DROP");
		flow.setName(flowName);
		flow.setIngressPort("2");
		
		client.addOrModifyFlow(flow, DPID, flow.getName());
		
		Assert.assertEquals(flow, client.getStaticFlow(DPID, flowName));
		
		client.deleteFlow(DPID, flowName);
		
		Assert.assertFalse(client.getStaticFlows(DPID).getFlowConfig().contains(flow));
	}
	
	@Test
	public void putAndDeleteOutputFlowTest() throws Exception {
		String flowName = "testPutAndDeleteOuput";
		
		FlowConfig flow = sampleFlow("OUTPUT=1");
		flow.setName(flowName);
		flow.setIngressPort("2");
		
		client.addOrModifyFlow(flow, DPID, flow.getName());
		
		
		FlowConfig retrieved =  client.getStaticFlow(DPID, flowName);
		Assert.assertEquals(flow, retrieved);
		
		client.deleteFlow(DPID, flowName);
		
		Assert.assertFalse(client.getStaticFlows(DPID).getFlowConfig().contains(flow));
	}
	
	@Test
	public void putAndDeleteMultiActionFlowTest() throws Exception {
		String flowName = "testPutAndDeleteMulti";
		
		FlowConfig flow = sampleFlow("OUTPUT=1", "SET_VLAN_ID=100");
		flow.setName(flowName);
		flow.setIngressPort("2");
		
		client.addOrModifyFlow(flow, DPID, flow.getName());
		
		
		FlowConfig retrieved =  client.getStaticFlow(DPID, flowName);
		Assert.assertEquals(flow, retrieved);
		
		client.deleteFlow(DPID, flowName);
		
		Assert.assertFalse(client.getStaticFlows(DPID).getFlowConfig().contains(flow));
	}
	
	
	
	@Test(expected=javax.ws.rs.NotFoundException.class)
	public void getUnexistingFlowFails() throws Exception {
		
		client.getStaticFlow(DPID, "unknown");
		
	}
	
	private static FlowConfig sampleFlow(String... actions) {
		FlowConfig flow = new FlowConfig();
		flow.setName(SAMPLE_FLOW_NAME);
		flow.setNode(new Node(DPID, "OF"));
		flow.setPriority("32767");
		
		flow.setIngressPort("1");
		flow.setActions(Arrays.asList(actions));
		return flow;
	}

}
