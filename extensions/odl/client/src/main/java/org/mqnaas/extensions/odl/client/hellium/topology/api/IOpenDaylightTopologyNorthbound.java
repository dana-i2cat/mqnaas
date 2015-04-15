package org.mqnaas.extensions.odl.client.hellium.topology.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.mqnaas.extensions.odl.hellium.topology.model.Topology;

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

/**
 * <p>
 * Interface representing the Opendaylight Topology Northbound API for the Helium release.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@Path("/controller/nb/v2/topology")
public interface IOpenDaylightTopologyNorthbound {

	@GET
	@Path("/{containerName}")
	@Consumes(MediaType.APPLICATION_XML)
	public Topology getTopology(@PathParam("containerName") String containerName);

}
