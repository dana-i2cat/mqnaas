package org.mqnaas.network.api.request;

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

import org.mqnaas.core.api.ICapability;
import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.annotations.AddsResource;
import org.mqnaas.network.api.IBaseNetworkManagement;
import org.mqnaas.network.api.exceptions.NetworkCreationException;

/**
 * A network management capability that uses network requests to create new
 * networks.
 * 
 * @author Georg Mansky-Kummert
 */
public interface IRequestBasedNetworkManagement extends IBaseNetworkManagement, ICapability {

	/**
	 * Returns the new network resource created, which is configured as defined
	 * in the given request.
	 */
	@AddsResource
	IRootResource createNetwork(IResource request) throws NetworkCreationException;


}
