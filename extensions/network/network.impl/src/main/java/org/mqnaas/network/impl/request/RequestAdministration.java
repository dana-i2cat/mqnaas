package org.mqnaas.network.impl.request;

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

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.annotations.Resource;
import org.mqnaas.core.api.exceptions.ApplicationActivationException;
import org.mqnaas.network.api.request.IRequestAdministration;
import org.mqnaas.network.api.request.Period;

/**
 * Implementation of the {@link IRequestAdministration} capability, which is bound to a {@link RequestResource}.
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestAdministration implements IRequestAdministration {

	@Resource
	IResource	resource;

	public static boolean isSupporting(IResource resource) {
		return resource instanceof RequestResource;
	}

	private Period	period;

	@Override
	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
	public Period getPeriod() {
		return period;
	}

	@Override
	public void activate() throws ApplicationActivationException {
	}

	@Override
	public void deactivate() {
	}

}
