package org.mqnaas.extensions.odl.client.switchnorthbound;

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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mqnaas.extensions.odl.client.switchnorthbound.api.NodeConnectors;
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

	@GET
	@Path("/{containerName}/node/{nodeType}/{nodeId}")
	@Produces(MediaType.APPLICATION_XML)
	public NodeConnectors getNodeConnectors(@PathParam("containerName") String containerName, @PathParam("nodeType") String nodeType,
			@PathParam("nodeId") String nodeId);
}
