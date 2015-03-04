package org.mqnaas.extensions.odl.capabilities.flows;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

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
import org.mqnaas.extensions.odl.client.hellium.flowprogrammer.api.model.FlowConfig;

public class FlowManagement implements IFlowManagement {
	
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
	public Collection<FlowConfig> getFlows() throws IllegalStateException, Exception {
		return new ArrayList<FlowConfig>(getFlowProgrammerClient().getStaticFlows().getFlowConfig());
	}

	@Override
	public Collection<FlowConfig> getFlows(String dpid) throws IllegalStateException, Exception {
		return getFlowProgrammerClient().getStaticFlows(dpid).getFlowConfig();
	}
	
	@Override
	public void addFlow(FlowConfig flow) throws BadRequestException {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void deleteFlow(String flowName) throws NotFoundException {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void updateFlow(String flowName, FlowConfig updated) throws NotFoundException {
		throw new UnsupportedOperationException("Not implemented");
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
