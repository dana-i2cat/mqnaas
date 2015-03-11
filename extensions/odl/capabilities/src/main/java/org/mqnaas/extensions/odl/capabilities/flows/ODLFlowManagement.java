package org.mqnaas.extensions.odl.capabilities.flows;

/*
 * #%L
 * MQNaaS :: ODL Capabilities
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

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.client.cxf.ICXFAPIProvider;
import org.mqnaas.clientprovider.api.apiclient.IAPIClientProviderFactory;
import org.mqnaas.clientprovider.exceptions.EndpointNotFoundException;
import org.mqnaas.clientprovider.exceptions.ProviderNotFoundException;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.extensions.odl.client.hellium.flowprogrammer.api.IOpenDaylightFlowProgrammerNorthbound;
import org.mqnaas.extensions.odl.hellium.flowprogrammer.model.FlowConfig;
import org.mqnaas.extensions.odl.hellium.flowprogrammer.model.FlowConfigs;

public class ODLFlowManagement implements IFlowManagement {
	
	@Resource
	IResource	network;
	
	@DependingOn
	private IAPIClientProviderFactory	apiProviderFactory;
	
	public static boolean isSupporting(IRootResource rootResource) {
		Type type = rootResource.getDescriptor().getSpecification().getType();
		
		return type == Type.NETWORK && StringUtils.equals(rootResource.getDescriptor().getSpecification().getModel(), "odl");
	}

	@Override
	public void activate() throws ApplicationActivationException {
		// fail fast when client is not available
		try {
			getFlowProgrammerClient();
		} catch (Exception e){
			throw new ApplicationActivationException("Required client is unavailable", e);
		}
	}

	@Override
	public void deactivate() {
	}

	@Override
	public FlowConfigs getAllFlows() throws IllegalStateException, Exception {
		return getFlowProgrammerClient().getStaticFlows();
	}

	@Override
	public FlowConfigs getFlows(String dpid) throws IllegalStateException, Exception {
		return getFlowProgrammerClient().getStaticFlows(dpid);
	}
	
	@Override
	public void addFlow(FlowConfig flow) throws IllegalStateException, Exception {
		getFlowProgrammerClient().addOrModifyFlow(flow, flow.getNode().getId(), flow.getName());
	}

	@Override
	public void deleteFlow(String dpid, String flowName) throws IllegalStateException, Exception {
		getFlowProgrammerClient().deleteFlow(dpid, flowName);
	}
	
	/**
	 * 
	 * @return
	 * @throws IllegalStateException when client is not available
	 */
	private IOpenDaylightFlowProgrammerNorthbound getFlowProgrammerClient() throws IllegalStateException {
		
		try {
			return apiProviderFactory.getAPIProvider(ICXFAPIProvider.class)
					.getAPIClient(network, IOpenDaylightFlowProgrammerNorthbound.class, null);
		} catch (EndpointNotFoundException e) {
			throw new IllegalStateException("Client unavailable", e);
		} catch (ProviderNotFoundException e) {
			throw new IllegalStateException("Client unavailable", e);
		}
	}
}
