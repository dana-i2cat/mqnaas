package org.mqnaas.extensions.odl.client.switchnorthbound;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mqnaas.extensions.odl.client.switchnorthbound.api.Nodes;

/**
 * Partial Switch Northbound API.
 * 
 * Based on OpenDaylight Helium release (<a href=
 * "https://github.com/opendaylight/controller/blob/stable/helium/opendaylight/northbound/switchmanager/src/main/java/org/opendaylight/controller/switchmanager/northbound/SwitchNorthbound.java"
 * >reference</a>).
 * 
 * @author Julio Carlos Barrera
 *
 */
@Path("/controller/nb/v2/switchmanager")
public interface ISwitchNorthboundAPI {

	@GET
	@Path("/{containerName}/nodes")
	@Produces(MediaType.APPLICATION_XML)
	public Nodes getNodes(@PathParam("containerName") String containerName);

	// TODO get node connectors

}
