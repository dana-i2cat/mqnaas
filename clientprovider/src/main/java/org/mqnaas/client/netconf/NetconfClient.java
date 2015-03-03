package org.mqnaas.client.netconf;

/*
 * #%L
 * MQNaaS :: Client Provider
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.mqnaas.core.api.Endpoint;
import org.mqnaas.core.api.credentials.Credentials;
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
