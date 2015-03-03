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

import java.util.concurrent.atomic.AtomicInteger;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.Specification.Type;

/**
 * <p>
 * Representation of a {@link IRootResource} inside a {@link Request} resource.
 * </p>
 * 
 * @author Georg Mansky-Kummert
 */
public class RequestRootResource implements IResource {

	private static AtomicInteger	ID_COUNTER	= new AtomicInteger();

	private String					id;

	private Type					type;

	public RequestRootResource(Type type) {
		id = "req_root-" + ID_COUNTER.incrementAndGet();
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append("[");

		sb.append("id=").append(id);
		sb.append(", type=").append(type);

		sb.append("]");
		return sb.toString();
	}

}
