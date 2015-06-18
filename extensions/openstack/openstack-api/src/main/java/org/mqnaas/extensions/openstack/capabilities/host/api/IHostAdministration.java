package org.mqnaas.extensions.openstack.capabilities.host.api;

/*
 * #%L
 * MQNaaS :: OpenStack API
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
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;

/**
 * <p>
 * Capability providing services to set and retrieve the main hardware attributes of a {@link Type#HOST} {@link IRootResource}
 * </p>
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public interface IHostAdministration extends ICapability {

	/**
	 * Returns the number of virtual CPUs of the host.
	 */
	int getNumberOfCpus();

	/**
	 * Returns the host amount of memory in MB.
	 */
	float getMemorySize();

	/**
	 * Returns the Disk size in GB.
	 */
	float getDiskSize();

	/**
	 * Returns the size of the Swap partition.
	 */
	String getSwapSize();

}
