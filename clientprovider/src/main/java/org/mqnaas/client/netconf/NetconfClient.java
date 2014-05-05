package org.mqnaas.client.netconf;

import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;

public class NetconfClient {

	private Credentials				c;
	private Endpoint				ep;
	private NetconfConfiguration	config;

	public NetconfClient(Endpoint ep, Credentials c, NetconfConfiguration config) {
		this.ep = ep;
		this.c = c;
		this.config = config;
	}

	public void doNetconfSpecificThing1() {
		System.out.println("Done netconf specific thing 1 on enpoint " + ep + " with credentials " + c + " and config " + config);
	}

	public void doNetconfSpecificThing2() {
		System.out.println("Done netconf specific thing 2 on enpoint " + ep + " with credentials " + c + " and config " + config);
	}

}
