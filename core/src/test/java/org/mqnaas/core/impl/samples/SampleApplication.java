package org.mqnaas.core.impl.samples;

/*
 * #%L
 * MQNaaS :: Core
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

import org.mqnaas.core.api.IApplication;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.impl.BindingManagement;
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
	public void activate() throws ApplicationActivationException {
		log.info("SampleApplication activated!");
	}

	@Override
	public void deactivate() {
		log.info("SampleApplication deactivated!");

	}

}
