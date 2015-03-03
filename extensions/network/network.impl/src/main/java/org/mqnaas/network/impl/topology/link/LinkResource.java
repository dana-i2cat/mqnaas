package org.mqnaas.network.impl.topology.link;

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

import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.mqnaas.core.api.IResource;

/**
 * Basic link resource implementation providing a simple unique id.
 * 
 * @author Georg Mansky-Kummert
 */
@XmlRootElement(namespace = "org.mqnaas")
@XmlAccessorType(XmlAccessType.FIELD)
public class LinkResource implements IResource {

	@XmlTransient
	private static AtomicInteger	ID_COUNTER	= new AtomicInteger();

	private String					id;

	public LinkResource() {
		id = "link-" + ID_COUNTER.incrementAndGet();
	}

	@Override
	public String getId() {
		return id;
	}

}
