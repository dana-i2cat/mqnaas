package org.mqnaas.network.impl.reservation;

/*
 * #%L
 * MQNaaS :: Network Implementation
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

import java.util.Set;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.reservation.IReservationAdministration;
import org.mqnaas.network.api.reservation.ReservationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class ReservationAdministration implements IReservationAdministration {

	private static final Logger	log	= LoggerFactory.getLogger(ReservationAdministration.class);

	private Period				period;
	private Set<IRootResource>	resources;
	private ReservationState	state;

	@Resource
	IResource					resource;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof ReservationResource;
	}

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing ReservationAdministration capability for resource " + resource.getId());

		state = ReservationState.CREATED;

		log.info("Initialized ReservationAdministration capability for resource " + resource.getId());
	}

	@Override
	public void deactivate() {
		log.info("Removing ReservationAdministration capability of resource " + resource.getId());

		log.info("Removing ReservationAdministration capability of resource " + resource.getId());

	}

	@Override
	public Period getPeriod() {
		return period;
	}

	@Override
	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
	public Set<IRootResource> getResources() {
		return resources;
	}

	@Override
	public void setResources(Set<IRootResource> resources) {
		this.resources = resources;
	}

	@Override
	public ReservationState getState() {
		return state;
	}

	@Override
	public void setState(ReservationState state) {
		this.state = state;
	}

}
