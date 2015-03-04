package org.mqnaas.extensions.odl.capabilities.flows;

import java.util.Collection;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mqnaas.core.api.ICapability;
import org.mqnaas.extensions.odl.client.hellium.flowprogrammer.api.model.FlowConfig;

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
	public Collection<FlowConfig> getFlows() throws Exception;
	
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
	public Collection<FlowConfig> getFlows(@PathParam("dpid") String dpid) throws NotFoundException, IllegalStateException, Exception;
	
	/**
	 * 
	 * @param flow
	 * @throws BadRequestException when flow is already configured
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void addFlow(FlowConfig flow) throws BadRequestException;
	
	/**
	 * 
	 * @param flowName
	 * @throws NotFoundException when given flowName is unknown
	 */
	@Path("/{flow}")
	@DELETE
	public void deleteFlow(@PathParam("flow") String flowName) throws NotFoundException;
	
	/**
	 * 
	 * @param flowName
	 * @param updated
	 * @throws NotFoundException when given flowName is unknown
	 */
	@Path("/{flow}")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public void updateFlow(@PathParam("flow") String flowName, FlowConfig updated) throws NotFoundException;
}
