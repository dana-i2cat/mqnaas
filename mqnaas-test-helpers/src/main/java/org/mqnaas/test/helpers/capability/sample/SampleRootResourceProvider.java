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

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IRootResourceProvider;
import org.mqnaas.core.api.Specification;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.core.api.exceptions.ResourceNotFoundException;

/**
 * Sample implementation {@link IRootResourceProvider} that only binds to {@link ResourceA}.
 * 
 * @author Julio Carlos Barrera (i2CAT Foundation)
 *
 */
public class SampleRootResourceProvider implements IRootResourceProvider {

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
	public List<IRootResource> getRootResources() {
		return null;
	}

	@Override
	public List<IRootResource> getRootResources(Type type, String model, String version) throws ResourceNotFoundException {
		return null;
	}

	@Override
	public IRootResource getRootResource(String id) throws ResourceNotFoundException {
		return null;
	}

	@Override
	public void setRootResources(Collection<IRootResource> rootResources) {
	}

}
