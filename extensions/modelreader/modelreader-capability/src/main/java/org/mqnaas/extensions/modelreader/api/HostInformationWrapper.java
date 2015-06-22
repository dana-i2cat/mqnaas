package org.mqnaas.extensions.modelreader.api;

/*
 * #%L
 * MQNaaS :: Generic Model Reader
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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.mqnaas.core.api.Specification.Type;

/**
 * <p>
 * Wrapper class containing the main hardware information of a {@link Type#HOST} resource.
 * </p>
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
@XmlType(name = "hostInformation")
@XmlAccessorType(XmlAccessType.FIELD)
public class HostInformationWrapper implements Serializable {

	private static final long	serialVersionUID	= 7615703041852542009L;

	private int					numberOfCPUs;
	private int					memorySize;
	private int					diskSize;
	private String				swapSize;

	public HostInformationWrapper() {

	}

	public HostInformationWrapper(int numberOfCPUs, int memorySize, int diskSize, String swap) {
		this.numberOfCPUs = numberOfCPUs;
		this.memorySize = memorySize;
		this.diskSize = diskSize;
		this.swapSize = swap;
	}

	public int getNumberOfCPUs() {
		return numberOfCPUs;
	}

	public void setNumberOfCPUs(int numberOfCPUs) {
		this.numberOfCPUs = numberOfCPUs;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public int getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(int diskSize) {
		this.diskSize = diskSize;
	}

	public String getSwapSize() {
		return swapSize;
	}

	public void setSwapSize(String swapSize) {
		this.swapSize = swapSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + diskSize;
		result = prime * result + memorySize;
		result = prime * result + numberOfCPUs;
		result = prime * result + ((swapSize == null) ? 0 : swapSize.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HostInformationWrapper other = (HostInformationWrapper) obj;
		if (diskSize != other.diskSize)
			return false;
		if (memorySize != other.memorySize)
			return false;
		if (numberOfCPUs != other.numberOfCPUs)
			return false;
		if (swapSize == null) {
			if (other.swapSize != null)
				return false;
		} else if (!swapSize.equals(other.swapSize))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HostInformationWrapper [numberOfCPUs=" + numberOfCPUs + ", memorySize=" + memorySize + ", diskSize=" + diskSize + ", swap=" + swapSize + "]";
	}

}
