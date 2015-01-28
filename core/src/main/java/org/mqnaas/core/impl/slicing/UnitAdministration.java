package org.mqnaas.core.impl.slicing;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.slicing.IUnitAdministration;
import org.mqnaas.core.api.slicing.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitAdministration implements IUnitAdministration {

	private static final Logger	log	= LoggerFactory.getLogger(UnitAdministration.class);

	@Resource
	IResource					resource;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof UnitResource;
	}

	private Range	range;

	@Override
	public void setRange(Range range) {
		this.range = range;
	}

	@Override
	public Range getRange() {
		return range;
	}

	@Override
	public void activate() throws ApplicationActivationException {
		log.info("Initializing UnitAdministration capability for resource " + resource.getId());

		log.info("Initialized UnitAdministration capability for resource " + resource.getId());

	}

	@Override
	public void deactivate() {
		log.info("Removing UnitAdministration capability for resource " + resource.getId());

		log.info("Removed UnitAdministration capability for resource " + resource.getId());
	}

}
