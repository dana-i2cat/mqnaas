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

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.extensions.odl.helium.flowprogrammer.model.FlowConfig;
import org.mqnaas.extensions.odl.helium.flowprogrammer.model.FlowConfigs;

@Path("/")
public interface IFlowManagement extends ICapability {
	
	/**
	 * 
	 * @return
	 * @throws IllegalStateException if required client is not available
	 * @throws Exception if there is any error while retrieving flows
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public FlowConfigs getAllFlows() throws Exception;
	
	/**
	 * 
	 * @param dpid
	 * @return
	 * @throws NotFoundException when given dpid is unknown
	 * @throws IllegalStateException if required client is not available
	 * @throws Exception if there is any error while retrieving flows
	 */
	@Path("/{dpid}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public FlowConfigs getFlows(@PathParam("dpid") String dpid) throws NotFoundException, IllegalStateException, Exception;
	
	/**
	 * 
	 * @param flow
	 * @throws BadRequestException when flow is already configured
	 * @throws IllegalStateException 
	 * @throws Exception if there is any error while configuring the flow
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void addFlow(FlowConfig flow) throws BadRequestException, IllegalStateException, Exception;
	
	/**
	 * 
	 * @param flowName
	 * @throws NotFoundException when given flowName is unknown
	 * @throws IllegalStateException
	 * @throws Exception if there is any error while deleting the flow
	 */
	@Path("/{flow}")
	@DELETE
	public void deleteFlow(@PathParam("dpid") String dpid, @PathParam("flow") String flowName) throws NotFoundException, IllegalStateException, Exception;
	
}
