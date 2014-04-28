package org.opennaas.core.client.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public interface IApplicationClient {

	@Path("A")
	@GET
	void methodA();

	@Path("B")
	@GET
	void methodB();

}
