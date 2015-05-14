package org.mqnaas.test.helpers.capability.sample;

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

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.annotations.DependingOn;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;

/**
 * Implementation of {@link ISampleCapability} that only binds to {@link ResourceA} with a private {@link IRootResourceProvider} attribute to be
 * injected using {@link @DependingOn} annotation.
 * 
 * @author Julio Carlos Barrera (i2CAT Foundation)
 *
 */
public class SampleCapabilityImpl implements ISampleCapability {

	@DependingOn
	private IRootResourceProvider	rootResourceProvider;

	public static boolean isSupporting(IRootResource resource) {
		Specification specification = resource.getDescriptor().getSpecification();

		return specification.getType() == ResourceA.RESOURCE_A_TYPE && StringUtils.equals(specification.getModel(), ResourceA.RESOURCE_A_MODEL);
	}

	@Override
	public void activate() throws ApplicationActivationException {
	}

	@Override
	public void deactivate() {
	}

	@Override
	public void sampleMethod() {
	}

}
