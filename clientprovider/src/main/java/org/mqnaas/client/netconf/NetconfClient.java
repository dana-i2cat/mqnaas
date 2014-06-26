package org.mqnaas.client.netconf;

import org.mqnaas.core.api.Credentials;
import org.mqnaas.core.api.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfClient {

	private static final Logger		log	= LoggerFactory.getLogger(NetconfClient.class);

	private Credentials				c;
	private Endpoint				ep;
	private NetconfConfiguration	config;

	public NetconfClient(Endpoint ep, Credentials c, NetconfConfiguration config) {
		this.ep = ep;
		this.c = c;
		this.config = config;
	}

	public void doNetconfSpecificThing1() {
		log.info("Done netconf specific thing 1 on enpoint " + ep + " with credentials " + c + " and config " + config);
	}

	public void doNetconfSpecificThing2() {
		log.info("Done netconf specific thing 2 on enpoint " + ep + " with credentials " + c + " and config " + config);
	}

}
