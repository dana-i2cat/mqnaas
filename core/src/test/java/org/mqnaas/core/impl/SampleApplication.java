package org.mqnaas.core.impl;

import org.mqnaas.core.api.IApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class SampleApplication implements IApplication {

	private static final Logger	log	= LoggerFactory.getLogger(BindingManagement.class);

	@Override
	public void activate() {
		log.info("SampleApplication activated!");
	}

	@Override
	public void deactivate() {
		log.info("SampleApplication deactivated!");

	}

}
