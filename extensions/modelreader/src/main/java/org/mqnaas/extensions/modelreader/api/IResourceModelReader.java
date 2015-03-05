package org.mqnaas.extensions.modelreader.api;

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

/**
 * <p>
 * Capability returning the model of a {@link IResource}.
 * </p>
 * <p>
 * The model of an <code>IResource</code> is defined and distributed among its {@link ICapability capabilities}. The goal of this capability is to
 * offer a general view of the resource state, by getting the information of all the capabilities of the resource.
 * </p>
 * 
 * @author Adrián Roselló Rey
 *
 */
public interface IResourceModelReader extends ICapability {

	public ResourceModelWrapper getResourceModel();

}
