package org.mqnaas.network.impl.reservation;

import java.util.Set;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.network.api.request.Period;
import org.mqnaas.network.api.reservation.IReservationAdministration;
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
