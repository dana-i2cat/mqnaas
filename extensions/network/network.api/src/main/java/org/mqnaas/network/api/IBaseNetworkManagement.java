package org.mqnaas.network.api;

/*
 * #%L
 * MQNaaS :: Network API
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

import java.util.Collection;

import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.annotations.ListsResources;
import org.mqnaas.core.api.annotations.RemovesResource;
import org.mqnaas.network.api.exceptions.NetworkReleaseException;

public interface IBaseNetworkManagement {

	/**
	 * Releases a previously created network
	 * 
	 * @throws NetworkReleaseException
	 */
	@RemovesResource
	void releaseNetwork(IRootResource resource) throws NetworkReleaseException;

	/**
	 * Returns all networks managed by this capability
	 */
	@ListsResources
	Collection<IRootResource> getNetworks();

}
